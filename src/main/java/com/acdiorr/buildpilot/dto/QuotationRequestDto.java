package com.acdiorr.buildpilot.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO used for CREATE and UPDATE quotation requests.
 * materialCost, subtotal and totalCost are calculated server-side.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationRequestDto {

    @NotNull(message = "Labor cost is required")
    @DecimalMin(value = "0.0", message = "Labor cost must be zero or positive")
    @Digits(integer = 12, fraction = 2, message = "Labor cost must have at most 12 integer digits and 2 decimal places")
    private BigDecimal laborCost;

    @NotNull(message = "Indirect cost is required")
    @DecimalMin(value = "0.0", message = "Indirect cost must be zero or positive")
    @Digits(integer = 12, fraction = 2, message = "Indirect cost must have at most 12 integer digits and 2 decimal places")
    private BigDecimal indirectCost;

    @NotNull(message = "Profit margin is required")
    @DecimalMin(value = "0.0", message = "Profit margin must be zero or positive")
    @Digits(integer = 5, fraction = 2, message = "Profit margin must have at most 5 integer digits and 2 decimal places")
    private BigDecimal profitMargin;

    private String notes;
}

