package com.acdiorr.buildpilot.dto;

import com.acdiorr.buildpilot.entity.enums.UnitMeasure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateMaterialResponseDto {

    private Long id;

    private Long constructionTemplateId;
    private String constructionTemplateName;
    private UnitMeasure constructionTemplateUnitType;

    private Long materialId;
    private String materialName;
    private UnitMeasure materialUnitMeasure;
    private BigDecimal materialUnitPrice;

    private BigDecimal quantityPerUnit;
    private BigDecimal wastePercentage;
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

