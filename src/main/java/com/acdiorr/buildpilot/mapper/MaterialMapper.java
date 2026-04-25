package com.acdiorr.buildpilot.mapper;

import com.acdiorr.buildpilot.dto.MaterialRequestDto;
import com.acdiorr.buildpilot.dto.MaterialResponseDto;
import com.acdiorr.buildpilot.entity.Material;
import org.springframework.stereotype.Component;

/**
 * Maps between Material entity and its DTOs.
 */
@Component
public class MaterialMapper {

    public Material toEntity(MaterialRequestDto dto) {
        return Material.builder()
                .name(dto.getName().trim())
                .materialType(dto.getMaterialType())
                .unitMeasure(dto.getUnitMeasure())
                .unitPrice(dto.getUnitPrice())
                .description(dto.getDescription())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
    }

    public MaterialResponseDto toResponseDto(Material material) {
        return MaterialResponseDto.builder()
                .id(material.getId())
                .name(material.getName())
                .materialType(material.getMaterialType())
                .unitMeasure(material.getUnitMeasure())
                .unitPrice(material.getUnitPrice())
                .description(material.getDescription())
                .isActive(material.getIsActive())
                .createdAt(material.getCreatedAt())
                .updatedAt(material.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDto(MaterialRequestDto dto, Material existing) {
        existing.setName(dto.getName().trim());
        existing.setMaterialType(dto.getMaterialType());
        existing.setUnitMeasure(dto.getUnitMeasure());
        existing.setUnitPrice(dto.getUnitPrice());
        existing.setDescription(dto.getDescription());
        if (dto.getIsActive() != null) {
            existing.setIsActive(dto.getIsActive());
        }
    }
}

