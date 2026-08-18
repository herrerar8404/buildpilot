package com.acdiorr.buildpilot.service;

import com.acdiorr.buildpilot.dto.TemplateMaterialRequestDto;
import com.acdiorr.buildpilot.dto.TemplateMaterialResponseDto;

import java.util.List;

public interface TemplateMaterialService {

    TemplateMaterialResponseDto createTemplateMaterial(Long constructionTemplateId,
                                                      Long materialId,
                                                      TemplateMaterialRequestDto request);

    List<TemplateMaterialResponseDto> getByConstructionTemplate(Long constructionTemplateId);

    List<TemplateMaterialResponseDto> getAllTemplateMaterials();

    TemplateMaterialResponseDto getById(Long id);

    TemplateMaterialResponseDto updateTemplateMaterial(Long id, TemplateMaterialRequestDto request);

    void deleteTemplateMaterial(Long id);
}

