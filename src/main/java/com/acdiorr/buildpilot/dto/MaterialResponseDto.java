package com.acdiorr.buildpilot.dto;

import com.acdiorr.buildpilot.entity.enums.MaterialType;
import com.acdiorr.buildpilot.entity.enums.UnitMeasure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO used for all material API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialResponseDto {

    private Long id;
    private String name;
    private MaterialType materialType;
    private UnitMeasure unitMeasure;
    private BigDecimal unitPrice;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

