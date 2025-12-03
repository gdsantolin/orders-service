package com.example.orders.client;

import com.example.orders.dto.response.OrderResponseDTO;
import com.example.orders.model.OrderStatus;
import com.example.orders.service.OrderStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalProductBClient {

    private final RestTemplate restTemplate;
    private final OrderStatusService orderStatusService;

    @Value("${external.product-b.url}")
    private String productBUrl;

    @Async("productBExecutor")
    public void sendOrderAsync(OrderResponseDTO order) {
        try {
            log.info("Sending order {} to Product B", order.getExternalOrderId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<OrderResponseDTO> request = new HttpEntity<>(order, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    productBUrl + "/orders",
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Order {} sent successfully to Product B", order.getExternalOrderId());
                orderStatusService.updateStatus(order.getExternalOrderId(), OrderStatus.SENT);
            } else {
                log.error("Product B returned error for order {}", order.getExternalOrderId());
                orderStatusService.updateStatus(order.getExternalOrderId(), OrderStatus.ERROR);
            }

        } catch (Exception e) {
            log.error("Error sending order {} to Product B", order.getExternalOrderId(), e);
            orderStatusService.updateStatus(order.getExternalOrderId(), OrderStatus.ERROR);
        }
    }
}