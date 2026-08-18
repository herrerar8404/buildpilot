package com.acdiorr.buildpilot.dto;

import com.acdiorr.buildpilot.entity.enums.ConstructionTemplateCategory;
import com.acdiorr.buildpilot.entity.enums.UnitMeasure;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConstructionTemplateRequestDto {

    @NotBlank(message = "Template name is required")
    private String name;

    private String description;

    @NotNull(message = "Template category is required")
    private ConstructionTemplateCategory category;

    @NotNull(message = "Template unit type is required")
    private UnitMeasure unitType;

    private Boolean active;
}

