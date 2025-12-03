package com.example.orders.controller;

import com.example.orders.dto.request.ExternalOrderRequestDTO;
import com.example.orders.dto.response.OrderResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/external-a")
@Slf4j
public class ExternalProductAMockController {

    private final RestTemplate restTemplate;

    public ExternalProductAMockController() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Simulates External Product A sending an order
     */
    @PostMapping("/send-order")
    public ResponseEntity<String> sendOrder(@RequestBody ExternalOrderRequestDTO order) {
        log.info("========================================");
        log.info("PRODUCT A SENDING ORDER");
        log.info("========================================");
        log.info("Order ID: {}", order.getOrderId());
        log.info("Items Count: {}", order.getItems().size());
        log.info("========================================");

        try {
            ResponseEntity<OrderResponseDTO> response = restTemplate.postForEntity(
                    "http://localhost:8080/api/orders",
                    order,
                    OrderResponseDTO.class
            );

            log.info("Order sent successfully. Status: {}", response.getStatusCode());
            return ResponseEntity.ok("Order sent to Order Service: " + order.getOrderId());

        } catch (Exception e) {
            log.error("Error sending order", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}