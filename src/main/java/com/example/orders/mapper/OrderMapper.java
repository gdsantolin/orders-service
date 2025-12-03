package com.example.orders.mapper;

import com.example.orders.dto.request.ExternalOrderRequestDTO;
import com.example.orders.dto.request.OrderItemRequestDTO;
import com.example.orders.dto.response.OrderItemResponseDTO;
import com.example.orders.dto.response.OrderResponseDTO;
import com.example.orders.dto.response.ProcessedOrderDTO;
import com.example.orders.dto.response.ProcessedOrderItemDTO;
import com.example.orders.model.Order;
import com.example.orders.model.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public Order toOrder(ExternalOrderRequestDTO dto) {
        Order order = new Order();
        order.setExternalOrderId(dto.getOrderId());

        List<OrderItem> items = dto.getItems().stream()
                .map(itemDto -> toOrderItem(itemDto, order))
                .collect(Collectors.toList());

        order.setItems(items);
        return order;
    }

    private OrderItem toOrderItem(OrderItemRequestDTO dto, Order order) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductCode(dto.getProductCode());
        item.setProductName(dto.getProductName());
        item.setUnitPrice(dto.getUnitPrice());
        item.setQuantity(dto.getQuantity());
        return item;
    }

    public OrderResponseDTO toResponseDTO(Order order) {
        List<OrderItemResponseDTO> itemDTOs = order.getItems().stream()
                .map(this::toItemResponseDTO)
                .collect(Collectors.toList());

        return OrderResponseDTO.builder()
                .id(order.getId())
                .externalOrderId(order.getExternalOrderId())
                .totalValue(order.getTotalValue())
                .status(order.getStatus())
                .items(itemDTOs)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemResponseDTO toItemResponseDTO(OrderItem item) {
        BigDecimal subtotal = item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return OrderItemResponseDTO.builder()
                .id(item.getId())
                .productCode(item.getProductCode())
                .productName(item.getProductName())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .build();
    }

    public ProcessedOrderDTO toProcessedOrderDTO(Order order) {
        List<ProcessedOrderItemDTO> itemDTOs = order.getItems().stream()
                .map(this::toProcessedOrderItemDTO)
                .collect(Collectors.toList());

        return ProcessedOrderDTO.builder()
                .orderId(order.getExternalOrderId())
                .totalValue(order.getTotalValue())
                .items(itemDTOs)
                .processedAt(order.getUpdatedAt())
                .build();
    }

    private ProcessedOrderItemDTO toProcessedOrderItemDTO(OrderItem item) {
        BigDecimal subtotal = item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return ProcessedOrderItemDTO.builder()
                .productCode(item.getProductCode())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(subtotal)
                .build();
    }
}
