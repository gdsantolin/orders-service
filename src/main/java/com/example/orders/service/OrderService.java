package com.example.orders.service;

import com.example.orders.client.ExternalProductBClient;
import com.example.orders.dto.request.ExternalOrderRequestDTO;
import com.example.orders.dto.response.OrderResponseDTO;
import com.example.orders.exception.DuplicateOrderException;
import com.example.orders.mapper.OrderMapper;
import com.example.orders.model.Order;
import com.example.orders.model.OrderStatus;
import com.example.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ExternalProductBClient productBClient;

    /**
     * Main method to process orders from External Product A
     * Coordinates transaction and async sending to Product B
     */
    public OrderResponseDTO processOrder(ExternalOrderRequestDTO requestDTO) {
        OrderResponseDTO response = processAndSaveOrder(requestDTO);

        // Send to Product B after transaction commit
        productBClient.sendOrderAsync(response);

        return response;
    }

    /**
     * Transactional method to process and save order
     * Separated to ensure transaction commits before async call
     */
    @Transactional
    private OrderResponseDTO processAndSaveOrder(ExternalOrderRequestDTO requestDTO) {
        log.info("Processing order: {}", requestDTO.getOrderId());

        if (orderRepository.existsByExternalOrderId(requestDTO.getOrderId())) {
            log.warn("Duplicate order detected: {}", requestDTO.getOrderId());
            throw new DuplicateOrderException("Order already exists: " + requestDTO.getOrderId());
        }

        Order order = orderMapper.toOrder(requestDTO);
        order.setStatus(OrderStatus.RECEIVED);

        BigDecimal totalValue = calculateTotalValue(order);
        order.setTotalValue(totalValue);
        order.setStatus(OrderStatus.PROCESSED);

        Order savedOrder = orderRepository.save(order);

        log.info("Order processed successfully: {} with total value: {}",
                savedOrder.getExternalOrderId(), totalValue);

        return orderMapper.toResponseDTO(savedOrder);
    }

    private BigDecimal calculateTotalValue(Order order) {
        return order.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public Optional<OrderResponseDTO> findByExternalOrderId(String externalOrderId) {
        log.debug("Searching order by external ID: {}", externalOrderId);

        return orderRepository.findByExternalOrderIdWithItems(externalOrderId)
                .map(orderMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> listOrders(OrderStatus status) {
        log.debug("Listing orders with status: {}", status);

        List<Order> orders;

        if (status != null) {
            orders = orderRepository.findByStatus(status);
        } else {
            orders = orderRepository.findAll();
        }

        return orders.stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
