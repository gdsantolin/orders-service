package com.example.orders.controller;

import com.example.orders.dto.request.ExternalOrderRequestDTO;
import com.example.orders.dto.response.ErrorResponseDTO;
import com.example.orders.dto.response.OrderResponseDTO;
import com.example.orders.exception.DuplicateOrderException;
import com.example.orders.model.OrderStatus;
import com.example.orders.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    /**
     * Endpoint to receive Orders from external product A
     * POST /api/orders
     */
    @PostMapping
    public ResponseEntity<?> receiveOrder(@RequestBody @Valid ExternalOrderRequestDTO request) {
        log.info("Received order request: {}", request.getOrderId());

        try {
            OrderResponseDTO response = orderService.processOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (DuplicateOrderException e) {
            log.error("Duplicate order: {}", request.getOrderId());

            ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                    .message(e.getMessage())
                    .error("Duplicate Order")
                    .status(HttpStatus.CONFLICT.value())
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);

        } catch (Exception e) {
            log.error("Error processing order: {}", request.getOrderId(), e);

            ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                    .message("Internal error processing order")
                    .error("Internal Server Error")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Endpoint to search a specific Order
     * GET /api/orders/{externalOrderId}
     */
    @GetMapping("/{externalOrderId}")
    public ResponseEntity<?> getOrder(@PathVariable String externalOrderId) {
        log.debug("Get order request: {}", externalOrderId);

        Optional<OrderResponseDTO> orderOpt = orderService.findByExternalOrderId(externalOrderId);

        if (orderOpt.isPresent()) {
            return ResponseEntity.ok(orderOpt.get());
        }

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .message("Order not found: " + externalOrderId)
                .error("Not Found")
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Endpoint to list all Orders (or filter by status)
     * GET /api/orders
     * GET /api/orders?status=PROCESSED
     */
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> listOrders(
            @RequestParam(required = false) OrderStatus status) {

        log.debug("List orders request with status: {}", status);

        List<OrderResponseDTO> orders = orderService.listOrders(status);
        return ResponseEntity.ok(orders);
    }
}
