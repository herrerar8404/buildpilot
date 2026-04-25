package com.acdiorr.buildpilot.dto;

import com.acdiorr.buildpilot.entity.enums.MaterialType;
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
 * DTO used for CREATE and UPDATE material requests (POST / PUT).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialRequestDto {

    @NotBlank(message = "Material name is required")
    private String name;

    @NotNull(message = "Material type is required")
    private MaterialType materialType;

    @NotNull(message = "Unit of measure is required")
    private UnitMeasure unitMeasure;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Unit price must have at most 10 integer digits and 2 decimal places")
    private BigDecimal unitPrice;

    private String description;

    private Boolean isActive;
}

