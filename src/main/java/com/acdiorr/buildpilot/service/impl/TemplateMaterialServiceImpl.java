package com.acdiorr.buildpilot.service.impl;

import com.acdiorr.buildpilot.dto.TemplateMaterialRequestDto;
import com.acdiorr.buildpilot.dto.TemplateMaterialResponseDto;
import com.acdiorr.buildpilot.entity.ConstructionTemplate;
import com.acdiorr.buildpilot.entity.Material;
import com.acdiorr.buildpilot.entity.TemplateMaterial;
import com.acdiorr.buildpilot.exception.*;
import com.acdiorr.buildpilot.mapper.TemplateMaterialMapper;
import com.acdiorr.buildpilot.repository.ConstructionTemplateRepository;
import com.acdiorr.buildpilot.repository.MaterialRepository;
import com.acdiorr.buildpilot.repository.TemplateMaterialRepository;
import com.acdiorr.buildpilot.service.TemplateMaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateMaterialServiceImpl implements TemplateMaterialService {

    private final TemplateMaterialRepository templateMaterialRepository;
    private final ConstructionTemplateRepository constructionTemplateRepository;
    private final MaterialRepository materialRepository;
    private final TemplateMaterialMapper templateMaterialMapper;

    @Override
    @Transactional
    public TemplateMaterialResponseDto createTemplateMaterial(Long constructionTemplateId,
                                                              Long materialId,
                                                              TemplateMaterialRequestDto request) {
        log.info("Assigning material id={} to construction template id={}", materialId, constructionTemplateId);

        ConstructionTemplate template = resolveTemplate(constructionTemplateId);
        Material material = resolveMaterial(materialId);

        if (templateMaterialRepository.existsByConstructionTemplateIdAndMaterialId(constructionTemplateId, materialId)) {
            throw new DuplicateTemplateMaterialException(constructionTemplateId, materialId);
        }

        TemplateMaterial toSave = templateMaterialMapper.toEntity(request, template, material);
        TemplateMaterial saved = templateMaterialRepository.save(toSave);
        return templateMaterialMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TemplateMaterialResponseDto> getByConstructionTemplate(Long constructionTemplateId) {
        log.info("Fetching template materials by construction template id={}", constructionTemplateId);
        if (!constructionTemplateRepository.existsById(constructionTemplateId)) {
            throw new ConstructionTemplateNotFoundException(constructionTemplateId);
        }

        return templateMaterialRepository.findByConstructionTemplateId(constructionTemplateId)
                .stream()
                .map(templateMaterialMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TemplateMaterialResponseDto> getAllTemplateMaterials() {
        log.info("Fetching all template materials");
        return templateMaterialRepository.findAll()
                .stream()
                .map(templateMaterialMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TemplateMaterialResponseDto getById(Long id) {
        log.info("Fetching template material id={}", id);
        return templateMaterialRepository.findDetailedById(id)
                .map(templateMaterialMapper::toResponseDto)
                .orElseThrow(() -> new TemplateMaterialNotFoundException(id));
    }

    @Override
    @Transactional
    public TemplateMaterialResponseDto updateTemplateMaterial(Long id, TemplateMaterialRequestDto request) {
        log.info("Updating template material id={}", id);

        TemplateMaterial existing = templateMaterialRepository.findDetailedById(id)
                .orElseThrow(() -> new TemplateMaterialNotFoundException(id));

        templateMaterialMapper.updateEntityFromDto(request, existing);
        TemplateMaterial saved = templateMaterialRepository.save(existing);
        return templateMaterialMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public void deleteTemplateMaterial(Long id) {
        log.info("Deleting template material id={}", id);
        if (!templateMaterialRepository.existsById(id)) {
            throw new TemplateMaterialNotFoundException(id);
        }
        templateMaterialRepository.deleteById(id);
    }

    private ConstructionTemplate resolveTemplate(Long id) {
        return constructionTemplateRepository.findById(id)
                .orElseThrow(() -> new ConstructionTemplateNotFoundException(id));
    }

    private Material resolveMaterial(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new MaterialNotFoundException(id));
    }
}

