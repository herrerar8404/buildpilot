package com.acdiorr.buildpilot.service.impl;

import com.acdiorr.buildpilot.dto.ApplyTemplateResponseDto;
import com.acdiorr.buildpilot.dto.ElementMaterialRequestDto;
import com.acdiorr.buildpilot.dto.ElementMaterialResponseDto;
import com.acdiorr.buildpilot.dto.QuotationResponseDto;
import com.acdiorr.buildpilot.entity.*;
import com.acdiorr.buildpilot.exception.ConstructionElementAlreadyMaterializedException;
import com.acdiorr.buildpilot.mapper.ConstructionElementMapper;
import com.acdiorr.buildpilot.repository.ConstructionElementRepository;
import com.acdiorr.buildpilot.repository.ConstructionTemplateRepository;
import com.acdiorr.buildpilot.repository.ElementMaterialRepository;
import com.acdiorr.buildpilot.repository.RoomRepository;
import com.acdiorr.buildpilot.service.ElementMaterialService;
import com.acdiorr.buildpilot.service.QuotationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConstructionElementServiceImplTest {

    @Mock
    private ConstructionElementRepository constructionElementRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ConstructionTemplateRepository constructionTemplateRepository;

    @Mock
    private ElementMaterialRepository elementMaterialRepository;

    @Mock
    private ElementMaterialService elementMaterialService;

    @Mock
    private QuotationService quotationService;

    @Mock
    private ConstructionElementMapper constructionElementMapper;

    @InjectMocks
    private ConstructionElementServiceImpl constructionElementService;

    @Test
    void applyTemplate_shouldGenerateElementMaterialsAndRecalculateQuotation() {
        Long elementId = 1L;
        Long templateId = 2L;
        Long projectId = 7L;
        Long materialId = 11L;

        Project project = Project.builder().id(projectId).build();
        Room room = Room.builder().id(4L).project(project).build();

        ConstructionElement element = ConstructionElement.builder()
                .id(elementId)
                .room(room)
                .calculatedArea(new BigDecimal("48.6"))
                .build();

        Material material = Material.builder().id(materialId).unitPrice(new BigDecimal("10.00")).build();

        TemplateMaterial templateMaterial = TemplateMaterial.builder()
                .id(30L)
                .material(material)
                .quantityPerUnit(new BigDecimal("12.5"))
                .wastePercentage(new BigDecimal("5.0"))
                .notes("Template-based material")
                .build();

        ConstructionTemplate template = ConstructionTemplate.builder()
                .id(templateId)
                .name("Block Wall")
                .templateMaterials(List.of(templateMaterial))
                .build();

        when(constructionElementRepository.findById(elementId)).thenReturn(Optional.of(element));
        when(constructionTemplateRepository.findById(templateId)).thenReturn(Optional.of(template));
        when(elementMaterialRepository.existsByConstructionElementId(elementId)).thenReturn(false);
        when(constructionElementRepository.save(any(ConstructionElement.class))).thenAnswer(i -> i.getArgument(0));

        when(elementMaterialService.createElementMaterial(eq(elementId), eq(materialId), any(ElementMaterialRequestDto.class)))
                .thenReturn(ElementMaterialResponseDto.builder()
                        .id(100L)
                        .calculatedCost(new BigDecimal("6378.75"))
                        .build());

        when(quotationService.recalculateProjectQuotationIfExists(projectId))
                .thenReturn(Optional.of(QuotationResponseDto.builder().totalCost(new BigDecimal("15000.99")).build()));

        ApplyTemplateResponseDto response = constructionElementService.applyTemplate(elementId, templateId);

        ArgumentCaptor<ElementMaterialRequestDto> requestCaptor = ArgumentCaptor.forClass(ElementMaterialRequestDto.class);
        verify(elementMaterialService).createElementMaterial(eq(elementId), eq(materialId), requestCaptor.capture());

        assertEquals(new BigDecimal("607.5000"), requestCaptor.getValue().getRequiredQuantity());
        assertEquals(1, response.getGeneratedMaterialsCount());
        assertEquals(new BigDecimal("6378.75"), response.getGeneratedMaterialCost());
        assertEquals(new BigDecimal("15000.99"), response.getQuotationTotalAfterRecalculation());
        assertEquals(templateId, response.getTemplateId());
    }

    @Test
    void applyTemplate_shouldBlockWhenElementAlreadyHasMaterials() {
        Long elementId = 5L;
        Long templateId = 6L;

        ConstructionElement element = ConstructionElement.builder()
                .id(elementId)
                .room(Room.builder().project(Project.builder().id(9L).build()).build())
                .calculatedArea(new BigDecimal("10"))
                .build();

        ConstructionTemplate template = ConstructionTemplate.builder()
                .id(templateId)
                .name("Concrete Slab")
                .templateMaterials(List.of())
                .build();

        when(constructionElementRepository.findById(elementId)).thenReturn(Optional.of(element));
        when(constructionTemplateRepository.findById(templateId)).thenReturn(Optional.of(template));
        when(elementMaterialRepository.existsByConstructionElementId(elementId)).thenReturn(true);

        assertThrows(ConstructionElementAlreadyMaterializedException.class,
                () -> constructionElementService.applyTemplate(elementId, templateId));

        verify(elementMaterialService, never()).createElementMaterial(any(), any(), any());
        verify(quotationService, never()).recalculateProjectQuotationIfExists(any());
    }
}

