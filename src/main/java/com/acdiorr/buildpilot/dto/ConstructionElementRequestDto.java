package com.acdiorr.buildpilot.dto;

import com.acdiorr.buildpilot.entity.enums.ElementType;
import com.acdiorr.buildpilot.entity.enums.UnitMeasure;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO used for CREATE and UPDATE construction element requests (POST / PUT).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConstructionElementRequestDto {

    @NotBlank(message = "Element name is required")
    private String name;

    @NotNull(message = "Element type is required")
    private ElementType elementType;

    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than zero")
    @Digits(integer = 10, fraction = 4, message = "Quantity must have at most 10 integer digits and 4 decimal places")
    private BigDecimal quantity;

    private UnitMeasure unitMeasure;

    @DecimalMin(value = "0.0", inclusive = false, message = "Calculated area must be greater than zero")
    @Digits(integer = 10, fraction = 4, message = "Calculated area must have at most 10 integer digits and 4 decimal places")
    private BigDecimal calculatedArea;

    private String description;
}

