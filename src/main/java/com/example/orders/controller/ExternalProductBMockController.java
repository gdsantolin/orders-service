package com.example.orders.controller;

import com.example.orders.dto.response.OrderResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mock controller to simulate External Product B
 * This simulates the external system receiving processed orders
 */
@RestController
@RequestMapping("/api/external-b")
@Slf4j
public class ExternalProductBMockController {

    @PostMapping("/orders")
    public ResponseEntity<String> receiveOrder(@RequestBody OrderResponseDTO order) {
        log.info("========================================");
        log.info("PRODUCT B RECEIVED ORDER");
        log.info("========================================");
        log.info("Order ID: {}", order.getExternalOrderId());
        log.info("Total Value: {}", order.getTotalValue());
        log.info("Status: {}", order.getStatus());
        log.info("Items Count: {}", order.getItems().size());
        log.info("========================================");

        return ResponseEntity.ok("Order " + order.getExternalOrderId() + " received successfully by Product B");
    }
}
