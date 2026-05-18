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
 * DTO used for CREATE and UPDATE ElementMaterial requests (POST / PUT).
 * <p>
 * {@code finalQuantity} and {@code calculatedCost} are intentionally excluded —
 * they are always computed server-side and never accepted from the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElementMaterialRequestDto {

    @NotNull(message = "Required quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Required quantity must be greater than zero")
    @Digits(integer = 10, fraction = 4, message = "Required quantity must have at most 10 integer digits and 4 decimal places")
    private BigDecimal requiredQuantity;

    @DecimalMin(value = "0.0", inclusive = false, message = "Performance per m² must be greater than zero")
    @Digits(integer = 10, fraction = 4, message = "Performance per m² must have at most 10 integer digits and 4 decimal places")
    private BigDecimal performancePerM2;

    /**
     * Stored as a percentage value (e.g. 5.00 = 5 %).
     * If not provided, 0 % is assumed — no waste.
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "Waste percentage must be zero or greater")
    @Digits(integer = 5, fraction = 2, message = "Waste percentage must have at most 5 integer digits and 2 decimal places")
    private BigDecimal wastePercentage;

    private String notes;
}

