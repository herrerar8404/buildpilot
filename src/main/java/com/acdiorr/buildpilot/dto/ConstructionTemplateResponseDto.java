package com.acdiorr.buildpilot.dto;

import com.acdiorr.buildpilot.entity.enums.ConstructionTemplateCategory;
import com.acdiorr.buildpilot.entity.enums.UnitMeasure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConstructionTemplateResponseDto {

    private Long id;
    private String name;
    private String description;
    private ConstructionTemplateCategory category;
    private UnitMeasure unitType;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer templateMaterialsCount;
}

