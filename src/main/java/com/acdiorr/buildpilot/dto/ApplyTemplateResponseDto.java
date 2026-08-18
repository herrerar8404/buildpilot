package com.acdiorr.buildpilot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response payload for template application over a construction element.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyTemplateResponseDto {

    private Long constructionElementId;
    private Long templateId;
    private String templateName;
    private Integer generatedMaterialsCount;
    private BigDecimal generatedMaterialCost;
    private BigDecimal quotationTotalAfterRecalculation;
}

