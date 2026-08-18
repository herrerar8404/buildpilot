package com.acdiorr.buildpilot.dto;

import com.acdiorr.buildpilot.entity.enums.ElementType;
import com.acdiorr.buildpilot.entity.enums.UnitMeasure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO used for all construction element API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConstructionElementResponseDto {

    private Long id;
    private String name;
    private ElementType elementType;
    private BigDecimal quantity;
    private UnitMeasure unitMeasure;
    private BigDecimal calculatedArea;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Room summary to avoid exposing full Room entity
    private Long roomId;
    private String roomName;

    // Optional template summary
    private Long templateId;
    private String templateName;
}

