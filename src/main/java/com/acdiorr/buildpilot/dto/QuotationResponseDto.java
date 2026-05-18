package com.acdiorr.buildpilot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO used for quotation API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationResponseDto {

    private Long id;

    private BigDecimal materialCost;
    private BigDecimal laborCost;
    private BigDecimal indirectCost;
    private BigDecimal profitMargin;
    private BigDecimal subtotal;
    private BigDecimal totalCost;
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long projectId;
    private String projectName;
}

