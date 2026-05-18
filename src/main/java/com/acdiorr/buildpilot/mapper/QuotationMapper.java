package com.acdiorr.buildpilot.mapper;

import com.acdiorr.buildpilot.dto.QuotationRequestDto;
import com.acdiorr.buildpilot.dto.QuotationResponseDto;
import com.acdiorr.buildpilot.entity.Project;
import com.acdiorr.buildpilot.entity.Quotation;
import org.springframework.stereotype.Component;

@Component
public class QuotationMapper {

    public Quotation toEntity(QuotationRequestDto dto, Project project) {
        return Quotation.builder()
                .project(project)
                .laborCost(dto.getLaborCost())
                .indirectCost(dto.getIndirectCost())
                .profitMargin(dto.getProfitMargin())
                .notes(dto.getNotes())
                .build();
    }

    public QuotationResponseDto toResponseDto(Quotation quotation) {
        return QuotationResponseDto.builder()
                .id(quotation.getId())
                .materialCost(quotation.getMaterialCost())
                .laborCost(quotation.getLaborCost())
                .indirectCost(quotation.getIndirectCost())
                .profitMargin(quotation.getProfitMargin())
                .subtotal(quotation.getSubtotal())
                .totalCost(quotation.getTotalCost())
                .notes(quotation.getNotes())
                .createdAt(quotation.getCreatedAt())
                .updatedAt(quotation.getUpdatedAt())
                .projectId(quotation.getProject().getId())
                .projectName(quotation.getProject().getProjectName())
                .build();
    }

    public void updateEntityFromDto(QuotationRequestDto dto, Quotation existing) {
        existing.setLaborCost(dto.getLaborCost());
        existing.setIndirectCost(dto.getIndirectCost());
        existing.setProfitMargin(dto.getProfitMargin());
        existing.setNotes(dto.getNotes());
    }
}

