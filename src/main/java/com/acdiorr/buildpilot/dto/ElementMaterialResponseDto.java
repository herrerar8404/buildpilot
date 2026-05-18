package com.acdiorr.buildpilot.dto;

import com.acdiorr.buildpilot.entity.enums.UnitMeasure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO used for all ElementMaterial API responses.
 * <p>
 * Includes denormalised material summary fields so callers never need to issue
 * a second request to the materials endpoint just to display a line item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElementMaterialResponseDto {

    private Long id;

    // ── Parent references ──────────────────────────────────────────────────────
    private Long constructionElementId;
    private String constructionElementName;

    // ── Material summary (denormalised for convenience) ───────────────────────
    private Long materialId;
    private String materialName;
    private UnitMeasure materialUnitMeasure;
    private BigDecimal materialUnitPrice;

    // ── Input fields ───────────────────────────────────────────────────────────
    private BigDecimal requiredQuantity;
    private BigDecimal performancePerM2;
    private BigDecimal wastePercentage;

    // ── Computed / persisted business results ─────────────────────────────────
    /** finalQuantity = requiredQuantity × (1 + wastePercentage / 100) */
    private BigDecimal finalQuantity;
    /** calculatedCost = finalQuantity × material.unitPrice */
    private BigDecimal calculatedCost;

    private String notes;

    // ── Audit ──────────────────────────────────────────────────────────────────
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

