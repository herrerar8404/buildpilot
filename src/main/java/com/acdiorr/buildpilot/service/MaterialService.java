package com.acdiorr.buildpilot.service;

import com.acdiorr.buildpilot.dto.MaterialRequestDto;
import com.acdiorr.buildpilot.dto.MaterialResponseDto;

import java.util.List;

public interface MaterialService {

    MaterialResponseDto createMaterial(MaterialRequestDto request);

    List<MaterialResponseDto> getAllMaterials();

    MaterialResponseDto getMaterialById(Long id);

    MaterialResponseDto updateMaterial(Long id, MaterialRequestDto request);

    void deleteMaterial(Long id);
}

