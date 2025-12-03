package com.example.orders.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedOrderDTO {

    private String orderId;
    private BigDecimal totalValue;
    private List<ProcessedOrderItemDTO> items;
    private LocalDateTime processedAt;
}
