package com.application.rest.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class PurchaseItemDTO {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer cant;
}