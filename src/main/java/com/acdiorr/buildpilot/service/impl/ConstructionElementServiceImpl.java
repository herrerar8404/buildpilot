package com.acdiorr.buildpilot.service.impl;

import com.acdiorr.buildpilot.dto.ApplyTemplateResponseDto;
import com.acdiorr.buildpilot.dto.ConstructionElementRequestDto;
import com.acdiorr.buildpilot.dto.ConstructionElementResponseDto;
import com.acdiorr.buildpilot.dto.ElementMaterialRequestDto;
import com.acdiorr.buildpilot.dto.QuotationResponseDto;
import com.acdiorr.buildpilot.entity.ConstructionTemplate;
import com.acdiorr.buildpilot.entity.ConstructionElement;
import com.acdiorr.buildpilot.entity.Room;
import com.acdiorr.buildpilot.entity.TemplateMaterial;
import com.acdiorr.buildpilot.exception.*;
import com.acdiorr.buildpilot.mapper.ConstructionElementMapper;
import com.acdiorr.buildpilot.repository.ConstructionTemplateRepository;
import com.acdiorr.buildpilot.repository.ConstructionElementRepository;
import com.acdiorr.buildpilot.repository.ElementMaterialRepository;
import com.acdiorr.buildpilot.repository.RoomRepository;
import com.acdiorr.buildpilot.service.ConstructionElementService;
import com.acdiorr.buildpilot.service.ElementMaterialService;
import com.acdiorr.buildpilot.service.QuotationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConstructionElementServiceImpl implements ConstructionElementService {

    private static final int QUANTITY_SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private final ConstructionElementRepository constructionElementRepository;
    private final RoomRepository roomRepository;
    private final ConstructionTemplateRepository constructionTemplateRepository;
    private final ElementMaterialRepository elementMaterialRepository;
    private final ElementMaterialService elementMaterialService;
    private final QuotationService quotationService;
    private final ConstructionElementMapper constructionElementMapper;

    @Override
    @Transactional
    public ConstructionElementResponseDto createConstructionElement(Long roomId, ConstructionElementRequestDto request) {
        log.info("Creating construction element in room id: {}", roomId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));

        ConstructionTemplate template = resolveTemplateOrNull(request.getTemplateId());

        ConstructionElement element = constructionElementMapper.toEntity(request, room, template);
        return constructionElementMapper.toResponseDto(constructionElementRepository.save(element));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConstructionElementResponseDto> getByRoom(Long roomId) {
        log.info("Fetching construction elements for room id: {}", roomId);
        if (!roomRepository.existsById(roomId)) {
            throw new RoomNotFoundException(roomId);
        }

        return constructionElementRepository.findByRoomId(roomId)
                .stream()
                .map(constructionElementMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ConstructionElementResponseDto getById(Long id) {
        log.info("Fetching construction element id: {}", id);
        return constructionElementRepository.findById(id)
                .map(constructionElementMapper::toResponseDto)
                .orElseThrow(() -> new ConstructionElementNotFoundException(id));
    }

    @Override
    @Transactional
    public ConstructionElementResponseDto updateConstructionElement(Long id, ConstructionElementRequestDto request) {
        log.info("Updating construction element id: {}", id);
        ConstructionElement existing = constructionElementRepository.findById(id)
                .orElseThrow(() -> new ConstructionElementNotFoundException(id));

        ConstructionTemplate template = resolveTemplateOrNull(request.getTemplateId());

        constructionElementMapper.updateEntityFromDto(request, existing, template);
        return constructionElementMapper.toResponseDto(constructionElementRepository.save(existing));
    }

    @Override
    @Transactional
    public ApplyTemplateResponseDto applyTemplate(Long constructionElementId, Long templateId) {
        log.info("Applying template id={} to construction element id={}", templateId, constructionElementId);

        ConstructionElement element = constructionElementRepository.findById(constructionElementId)
                .orElseThrow(() -> new ConstructionElementNotFoundException(constructionElementId));

        ConstructionTemplate template = constructionTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ConstructionTemplateNotFoundException(templateId));

        // Safety-first strategy: block if materials already exist to avoid destructive data loss.
        if (elementMaterialRepository.existsByConstructionElementId(constructionElementId)) {
            throw new ConstructionElementAlreadyMaterializedException(constructionElementId);
        }

        element.setConstructionTemplate(template);
        constructionElementRepository.save(element);

        List<TemplateMaterial> templateMaterials = template.getTemplateMaterials();
        BigDecimal area = normalizeArea(element.getCalculatedArea());
        int generatedCount = 0;
        BigDecimal generatedCost = BigDecimal.ZERO;

        for (TemplateMaterial tm : templateMaterials) {
            BigDecimal requiredQuantity = area.multiply(tm.getQuantityPerUnit())
                    .setScale(QUANTITY_SCALE, ROUNDING);

            ElementMaterialRequestDto request = ElementMaterialRequestDto.builder()
                    .requiredQuantity(requiredQuantity)
                    .performancePerM2(tm.getQuantityPerUnit())
                    .wastePercentage(tm.getWastePercentage())
                    .notes(tm.getNotes())
                    .build();

            generatedCost = generatedCost.add(
                    elementMaterialService
                            .createElementMaterial(constructionElementId, tm.getMaterial().getId(), request)
                            .getCalculatedCost()
            );
            generatedCount++;
        }

        Long projectId = element.getRoom().getProject().getId();
        BigDecimal quotationTotal = quotationService.recalculateProjectQuotationIfExists(projectId)
                .map(QuotationResponseDto::getTotalCost)
                .orElse(null);

        return ApplyTemplateResponseDto.builder()
                .constructionElementId(constructionElementId)
                .templateId(templateId)
                .templateName(template.getName())
                .generatedMaterialsCount(generatedCount)
                .generatedMaterialCost(generatedCost.setScale(2, ROUNDING))
                .quotationTotalAfterRecalculation(quotationTotal)
                .build();
    }

    @Override
    @Transactional
    public void deleteConstructionElement(Long id) {
        log.info("Deleting construction element id: {}", id);
        if (!constructionElementRepository.existsById(id)) {
            throw new ConstructionElementNotFoundException(id);
        }
        constructionElementRepository.deleteById(id);
    }

    private ConstructionTemplate resolveTemplateOrNull(Long templateId) {
        if (templateId == null) {
            return null;
        }
        return constructionTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ConstructionTemplateNotFoundException(templateId));
    }

    private BigDecimal normalizeArea(BigDecimal calculatedArea) {
        if (calculatedArea == null || calculatedArea.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTemplateApplicationException(
                    "Construction element calculatedArea must be greater than zero to apply a template"
            );
        }
        return calculatedArea;
    }
}

