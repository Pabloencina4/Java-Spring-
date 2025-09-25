package com.application.rest.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class PurchaseDTO {
    private Long id;
    private String username;
    private BigDecimal total;
    private LocalDateTime compraDate;
    private List<PurchaseItemDTO> items = new ArrayList<>();


}
