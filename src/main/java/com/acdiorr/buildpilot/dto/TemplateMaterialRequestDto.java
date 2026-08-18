package com.acdiorr.buildpilot.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateMaterialRequestDto {

    @NotNull(message = "Quantity per unit is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity per unit must be greater than zero")
    @Digits(integer = 10, fraction = 4, message = "Quantity per unit must have at most 10 integer digits and 4 decimal places")
    private BigDecimal quantityPerUnit;

    @DecimalMin(value = "0.0", inclusive = true, message = "Waste percentage must be zero or greater")
    @Digits(integer = 5, fraction = 2, message = "Waste percentage must have at most 5 integer digits and 2 decimal places")
    private BigDecimal wastePercentage;

    private String notes;
}

