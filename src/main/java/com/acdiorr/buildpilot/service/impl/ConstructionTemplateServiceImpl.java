package com.acdiorr.buildpilot.service.impl;

import com.acdiorr.buildpilot.dto.ConstructionTemplateRequestDto;
import com.acdiorr.buildpilot.dto.ConstructionTemplateResponseDto;
import com.acdiorr.buildpilot.entity.ConstructionTemplate;
import com.acdiorr.buildpilot.exception.ConstructionTemplateDuplicateNameException;
import com.acdiorr.buildpilot.exception.ConstructionTemplateNotFoundException;
import com.acdiorr.buildpilot.mapper.ConstructionTemplateMapper;
import com.acdiorr.buildpilot.repository.ConstructionTemplateRepository;
import com.acdiorr.buildpilot.service.ConstructionTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConstructionTemplateServiceImpl implements ConstructionTemplateService {

    private final ConstructionTemplateRepository constructionTemplateRepository;
    private final ConstructionTemplateMapper constructionTemplateMapper;

    @Override
    @Transactional
    public ConstructionTemplateResponseDto createConstructionTemplate(ConstructionTemplateRequestDto request) {
        log.info("Creating construction template: {}", request.getName());
        String normalizedName = request.getName().trim();
        if (constructionTemplateRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new ConstructionTemplateDuplicateNameException(normalizedName);
        }

        ConstructionTemplate template = constructionTemplateMapper.toEntity(request);
        template.setName(normalizedName);
        return constructionTemplateMapper.toResponseDto(constructionTemplateRepository.save(template));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConstructionTemplateResponseDto> getAllConstructionTemplates() {
        log.info("Fetching all construction templates");
        return constructionTemplateRepository.findAll()
                .stream()
                .map(constructionTemplateMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ConstructionTemplateResponseDto getConstructionTemplateById(Long id) {
        log.info("Fetching construction template id: {}", id);
        return constructionTemplateRepository.findById(id)
                .map(constructionTemplateMapper::toResponseDto)
                .orElseThrow(() -> new ConstructionTemplateNotFoundException(id));
    }

    @Override
    @Transactional
    public ConstructionTemplateResponseDto updateConstructionTemplate(Long id, ConstructionTemplateRequestDto request) {
        log.info("Updating construction template id: {}", id);

        ConstructionTemplate existing = constructionTemplateRepository.findById(id)
                .orElseThrow(() -> new ConstructionTemplateNotFoundException(id));

        String normalizedName = request.getName().trim();
        if (!existing.getName().equalsIgnoreCase(normalizedName)
                && constructionTemplateRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new ConstructionTemplateDuplicateNameException(normalizedName);
        }

        constructionTemplateMapper.updateEntityFromDto(request, existing);
        existing.setName(normalizedName);
        return constructionTemplateMapper.toResponseDto(constructionTemplateRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteConstructionTemplate(Long id) {
        log.info("Deleting construction template id: {}", id);
        if (!constructionTemplateRepository.existsById(id)) {
            throw new ConstructionTemplateNotFoundException(id);
        }
        constructionTemplateRepository.deleteById(id);
    }
}

