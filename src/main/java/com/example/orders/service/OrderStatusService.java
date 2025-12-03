package com.example.orders.service;

import com.example.orders.model.OrderStatus;
import com.example.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStatusService {

    private final OrderRepository orderRepository;

    @Transactional
    public void updateStatus(String externalOrderId, OrderStatus status) {
        orderRepository.findByExternalOrderId(externalOrderId)
                .ifPresent(order -> {
                    order.setStatus(status);
                    orderRepository.save(order);
                    log.debug("Updated order {} status to {}", externalOrderId, status);
                });
    }
}
