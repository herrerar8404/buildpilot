package com.acdiorr.buildpilot.mapper;

import com.acdiorr.buildpilot.dto.ConstructionElementRequestDto;
import com.acdiorr.buildpilot.dto.ConstructionElementResponseDto;
import com.acdiorr.buildpilot.entity.ConstructionTemplate;
import com.acdiorr.buildpilot.entity.ConstructionElement;
import com.acdiorr.buildpilot.entity.Room;
import org.springframework.stereotype.Component;

/**
 * Maps between ConstructionElement entity and its DTOs.
 */
@Component
public class ConstructionElementMapper {

    public ConstructionElement toEntity(ConstructionElementRequestDto dto, Room room, ConstructionTemplate template) {
        return ConstructionElement.builder()
                .name(dto.getName().trim())
                .elementType(dto.getElementType())
                .quantity(dto.getQuantity())
                .unitMeasure(dto.getUnitMeasure())
                .calculatedArea(dto.getCalculatedArea())
                .description(dto.getDescription())
                .room(room)
                .constructionTemplate(template)
                .build();
    }

    public ConstructionElementResponseDto toResponseDto(ConstructionElement element) {
        return ConstructionElementResponseDto.builder()
                .id(element.getId())
                .name(element.getName())
                .elementType(element.getElementType())
                .quantity(element.getQuantity())
                .unitMeasure(element.getUnitMeasure())
                .calculatedArea(element.getCalculatedArea())
                .description(element.getDescription())
                .createdAt(element.getCreatedAt())
                .updatedAt(element.getUpdatedAt())
                .roomId(element.getRoom().getId())
                .roomName(element.getRoom().getName())
                .templateId(element.getConstructionTemplate() != null ? element.getConstructionTemplate().getId() : null)
                .templateName(element.getConstructionTemplate() != null ? element.getConstructionTemplate().getName() : null)
                .build();
    }

    public void updateEntityFromDto(ConstructionElementRequestDto dto,
                                    ConstructionElement existing,
                                    ConstructionTemplate template) {
        existing.setName(dto.getName().trim());
        existing.setElementType(dto.getElementType());
        existing.setQuantity(dto.getQuantity());
        existing.setUnitMeasure(dto.getUnitMeasure());
        existing.setCalculatedArea(dto.getCalculatedArea());
        existing.setDescription(dto.getDescription());
        existing.setConstructionTemplate(template);
    }
}

