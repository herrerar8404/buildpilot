package com.acdiorr.buildpilot.service.impl;

import com.acdiorr.buildpilot.dto.QuotationRequestDto;
import com.acdiorr.buildpilot.dto.QuotationResponseDto;
import com.acdiorr.buildpilot.entity.Project;
import com.acdiorr.buildpilot.entity.Quotation;
import com.acdiorr.buildpilot.exception.DuplicateQuotationException;
import com.acdiorr.buildpilot.exception.QuotationNotFoundException;
import com.acdiorr.buildpilot.mapper.QuotationMapper;
import com.acdiorr.buildpilot.repository.ElementMaterialRepository;
import com.acdiorr.buildpilot.repository.ProjectRepository;
import com.acdiorr.buildpilot.repository.QuotationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuotationServiceImplTest {

    @Mock
    private QuotationRepository quotationRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ElementMaterialRepository elementMaterialRepository;

    @Mock
    private QuotationMapper quotationMapper;

    @InjectMocks
    private QuotationServiceImpl quotationService;

    @Test
    void createQuotation_shouldCalculateAndPersistFinancialFields() {
        Long projectId = 1L;

        Project project = Project.builder().id(projectId).projectName("House Alpha").build();
        QuotationRequestDto request = QuotationRequestDto.builder()
                .laborCost(new BigDecimal("200"))
                .indirectCost(new BigDecimal("50"))
                .profitMargin(new BigDecimal("10"))
                .notes("Initial quotation")
                .build();

        Quotation quotation = Quotation.builder()
                .project(project)
                .laborCost(new BigDecimal("200"))
                .indirectCost(new BigDecimal("50"))
                .profitMargin(new BigDecimal("10"))
                .notes("Initial quotation")
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(quotationRepository.existsByProjectId(projectId)).thenReturn(false);
        when(quotationMapper.toEntity(request, project)).thenReturn(quotation);
        when(elementMaterialRepository.sumCalculatedCostByProjectId(projectId)).thenReturn(new BigDecimal("100.255"));

        when(quotationRepository.save(any(Quotation.class))).thenAnswer(invocation -> {
            Quotation saved = invocation.getArgument(0);
            saved.setId(99L);
            return saved;
        });

        when(quotationMapper.toResponseDto(any(Quotation.class))).thenAnswer(invocation -> {
            Quotation saved = invocation.getArgument(0);
            return QuotationResponseDto.builder()
                    .id(saved.getId())
                    .materialCost(saved.getMaterialCost())
                    .laborCost(saved.getLaborCost())
                    .indirectCost(saved.getIndirectCost())
                    .profitMargin(saved.getProfitMargin())
                    .subtotal(saved.getSubtotal())
                    .totalCost(saved.getTotalCost())
                    .projectId(saved.getProject().getId())
                    .build();
        });

        QuotationResponseDto result = quotationService.createQuotation(projectId, request);

        assertEquals(new BigDecimal("100.26"), result.getMaterialCost());
        assertEquals(new BigDecimal("350.26"), result.getSubtotal());
        assertEquals(new BigDecimal("385.29"), result.getTotalCost());
        assertEquals(new BigDecimal("10.00"), result.getProfitMargin());
        assertEquals(99L, result.getId());

        verify(elementMaterialRepository).sumCalculatedCostByProjectId(projectId);
        verify(quotationRepository).save(any(Quotation.class));
    }

    @Test
    void updateQuotation_shouldRecalculateTotalsUsingProjectMaterialSum() {
        Long quotationId = 10L;
        Long projectId = 2L;

        Project project = Project.builder().id(projectId).projectName("Tower Beta").build();
        Quotation existing = Quotation.builder()
                .id(quotationId)
                .project(project)
                .laborCost(new BigDecimal("0"))
                .indirectCost(new BigDecimal("0"))
                .profitMargin(new BigDecimal("0"))
                .build();

        QuotationRequestDto request = QuotationRequestDto.builder()
                .laborCost(new BigDecimal("300"))
                .indirectCost(new BigDecimal("25.555"))
                .profitMargin(new BigDecimal("12.5"))
                .notes("Revised quotation")
                .build();

        when(quotationRepository.findById(quotationId)).thenReturn(Optional.of(existing));
        doAnswer(invocation -> {
            QuotationRequestDto dto = invocation.getArgument(0);
            Quotation target = invocation.getArgument(1);
            target.setLaborCost(dto.getLaborCost());
            target.setIndirectCost(dto.getIndirectCost());
            target.setProfitMargin(dto.getProfitMargin());
            target.setNotes(dto.getNotes());
            return null;
        }).when(quotationMapper).updateEntityFromDto(request, existing);

        when(elementMaterialRepository.sumCalculatedCostByProjectId(projectId)).thenReturn(null);
        when(quotationRepository.save(any(Quotation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(quotationMapper.toResponseDto(any(Quotation.class))).thenAnswer(invocation -> {
            Quotation saved = invocation.getArgument(0);
            return QuotationResponseDto.builder()
                    .id(saved.getId())
                    .materialCost(saved.getMaterialCost())
                    .subtotal(saved.getSubtotal())
                    .totalCost(saved.getTotalCost())
                    .profitMargin(saved.getProfitMargin())
                    .build();
        });

        QuotationResponseDto result = quotationService.updateQuotation(quotationId, request);

        assertEquals(new BigDecimal("0.00"), result.getMaterialCost());
        assertEquals(new BigDecimal("325.56"), result.getSubtotal());
        assertEquals(new BigDecimal("366.26"), result.getTotalCost());
        assertEquals(new BigDecimal("12.50"), result.getProfitMargin());

        verify(elementMaterialRepository).sumCalculatedCostByProjectId(projectId);
    }

    @Test
    void createQuotation_shouldRejectDuplicateQuotationPerProject() {
        Long projectId = 3L;
        Project project = Project.builder().id(projectId).build();

        QuotationRequestDto request = QuotationRequestDto.builder()
                .laborCost(new BigDecimal("100"))
                .indirectCost(new BigDecimal("20"))
                .profitMargin(new BigDecimal("5"))
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(quotationRepository.existsByProjectId(projectId)).thenReturn(true);

        assertThrows(DuplicateQuotationException.class,
                () -> quotationService.createQuotation(projectId, request));

        verify(quotationRepository, never()).save(any(Quotation.class));
    }

    @Test
    void getByProject_shouldThrowWhenProjectHasNoQuotation() {
        Long projectId = 4L;

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(quotationRepository.findByProjectId(projectId)).thenReturn(Optional.empty());

        assertThrows(QuotationNotFoundException.class,
                () -> quotationService.getByProject(projectId));
    }
}

