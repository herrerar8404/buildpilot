package com.acdiorr.buildpilot.mapper;

import com.acdiorr.buildpilot.dto.ElementMaterialRequestDto;
import com.acdiorr.buildpilot.dto.ElementMaterialResponseDto;
import com.acdiorr.buildpilot.entity.ConstructionElement;
import com.acdiorr.buildpilot.entity.ElementMaterial;
import com.acdiorr.buildpilot.entity.Material;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Maps between ElementMaterial entity and its DTOs.
 * Calculated fields (finalQuantity, calculatedCost) are NOT populated here —
 * that responsibility belongs to the service layer.
 */
@Component
public class ElementMaterialMapper {

    /**
     * Build a new ElementMaterial entity from the DTO plus the resolved parent entities.
     * Relationships and computed fields are set separately by the service.
     */
    public ElementMaterial toEntity(ElementMaterialRequestDto dto,
                                    ConstructionElement constructionElement,
                                    Material material) {
        return ElementMaterial.builder()
                .constructionElement(constructionElement)
                .material(material)
                .requiredQuantity(dto.getRequiredQuantity())
                .performancePerM2(dto.getPerformancePerM2())
                .wastePercentage(dto.getWastePercentage())
                .notes(dto.getNotes())
                .build();
    }

    public ElementMaterialResponseDto toResponseDto(ElementMaterial em) {
        return ElementMaterialResponseDto.builder()
                .id(em.getId())
                // parent element
                .constructionElementId(em.getConstructionElement().getId())
                .constructionElementName(em.getConstructionElement().getName())
                // material summary
                .materialId(em.getMaterial().getId())
                .materialName(em.getMaterial().getName())
                .materialUnitMeasure(em.getMaterial().getUnitMeasure())
                .materialUnitPrice(em.getMaterial().getUnitPrice())
                // input fields
                .requiredQuantity(em.getRequiredQuantity())
                .performancePerM2(em.getPerformancePerM2())
                .wastePercentage(em.getWastePercentage())
                // computed results
                .finalQuantity(em.getFinalQuantity())
                .calculatedCost(em.getCalculatedCost())
                .notes(em.getNotes())
                // audit
                .createdAt(em.getCreatedAt())
                .updatedAt(em.getUpdatedAt())
                .build();
    }

    /**
     * Applies only the mutable input fields from the DTO to an existing entity.
     * Does NOT touch constructionElement, material, finalQuantity, or calculatedCost —
     * the service layer recalculates those after calling this method.
     */
    public void updateEntityFromDto(ElementMaterialRequestDto dto, ElementMaterial existing) {
        existing.setRequiredQuantity(dto.getRequiredQuantity());
        existing.setPerformancePerM2(dto.getPerformancePerM2());
        existing.setWastePercentage(dto.getWastePercentage() != null
                ? dto.getWastePercentage()
                : BigDecimal.ZERO);
        existing.setNotes(dto.getNotes());
    }
}

