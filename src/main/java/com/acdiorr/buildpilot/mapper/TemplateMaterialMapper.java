package com.acdiorr.buildpilot.mapper;

import com.acdiorr.buildpilot.dto.TemplateMaterialRequestDto;
import com.acdiorr.buildpilot.dto.TemplateMaterialResponseDto;
import com.acdiorr.buildpilot.entity.ConstructionTemplate;
import com.acdiorr.buildpilot.entity.Material;
import com.acdiorr.buildpilot.entity.TemplateMaterial;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TemplateMaterialMapper {

    public TemplateMaterial toEntity(TemplateMaterialRequestDto dto,
                                     ConstructionTemplate constructionTemplate,
                                     Material material) {
        return TemplateMaterial.builder()
                .constructionTemplate(constructionTemplate)
                .material(material)
                .quantityPerUnit(dto.getQuantityPerUnit())
                .wastePercentage(dto.getWastePercentage() != null ? dto.getWastePercentage() : BigDecimal.ZERO)
                .notes(dto.getNotes())
                .build();
    }

    public TemplateMaterialResponseDto toResponseDto(TemplateMaterial templateMaterial) {
        return TemplateMaterialResponseDto.builder()
                .id(templateMaterial.getId())
                .constructionTemplateId(templateMaterial.getConstructionTemplate().getId())
                .constructionTemplateName(templateMaterial.getConstructionTemplate().getName())
                .constructionTemplateUnitType(templateMaterial.getConstructionTemplate().getUnitType())
                .materialId(templateMaterial.getMaterial().getId())
                .materialName(templateMaterial.getMaterial().getName())
                .materialUnitMeasure(templateMaterial.getMaterial().getUnitMeasure())
                .materialUnitPrice(templateMaterial.getMaterial().getUnitPrice())
                .quantityPerUnit(templateMaterial.getQuantityPerUnit())
                .wastePercentage(templateMaterial.getWastePercentage())
                .notes(templateMaterial.getNotes())
                .createdAt(templateMaterial.getCreatedAt())
                .updatedAt(templateMaterial.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDto(TemplateMaterialRequestDto dto, TemplateMaterial existing) {
        existing.setQuantityPerUnit(dto.getQuantityPerUnit());
        existing.setWastePercentage(dto.getWastePercentage() != null ? dto.getWastePercentage() : BigDecimal.ZERO);
        existing.setNotes(dto.getNotes());
    }
}

