package com.acdiorr.buildpilot.mapper;

import com.acdiorr.buildpilot.dto.ConstructionTemplateRequestDto;
import com.acdiorr.buildpilot.dto.ConstructionTemplateResponseDto;
import com.acdiorr.buildpilot.entity.ConstructionTemplate;
import org.springframework.stereotype.Component;

@Component
public class ConstructionTemplateMapper {

    public ConstructionTemplate toEntity(ConstructionTemplateRequestDto dto) {
        return ConstructionTemplate.builder()
                .name(dto.getName().trim())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .unitType(dto.getUnitType())
                .active(dto.getActive() != null ? dto.getActive() : Boolean.TRUE)
                .build();
    }

    public ConstructionTemplateResponseDto toResponseDto(ConstructionTemplate template) {
        return ConstructionTemplateResponseDto.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .category(template.getCategory())
                .unitType(template.getUnitType())
                .active(template.getActive())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .templateMaterialsCount(template.getTemplateMaterials() != null ? template.getTemplateMaterials().size() : 0)
                .build();
    }

    public void updateEntityFromDto(ConstructionTemplateRequestDto dto, ConstructionTemplate existing) {
        existing.setName(dto.getName().trim());
        existing.setDescription(dto.getDescription());
        existing.setCategory(dto.getCategory());
        existing.setUnitType(dto.getUnitType());
        existing.setActive(dto.getActive() != null ? dto.getActive() : existing.getActive());
    }
}

