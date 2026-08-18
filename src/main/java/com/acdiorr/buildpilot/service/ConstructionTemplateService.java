package com.acdiorr.buildpilot.service;

import com.acdiorr.buildpilot.dto.ConstructionTemplateRequestDto;
import com.acdiorr.buildpilot.dto.ConstructionTemplateResponseDto;

import java.util.List;

public interface ConstructionTemplateService {

    ConstructionTemplateResponseDto createConstructionTemplate(ConstructionTemplateRequestDto request);

    List<ConstructionTemplateResponseDto> getAllConstructionTemplates();

    ConstructionTemplateResponseDto getConstructionTemplateById(Long id);

    ConstructionTemplateResponseDto updateConstructionTemplate(Long id, ConstructionTemplateRequestDto request);

    void deleteConstructionTemplate(Long id);
}

