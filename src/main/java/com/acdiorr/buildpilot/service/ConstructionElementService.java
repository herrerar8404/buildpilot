package com.acdiorr.buildpilot.service;

import com.acdiorr.buildpilot.dto.ConstructionElementRequestDto;
import com.acdiorr.buildpilot.dto.ConstructionElementResponseDto;

import java.util.List;

public interface ConstructionElementService {

    ConstructionElementResponseDto createConstructionElement(Long roomId, ConstructionElementRequestDto request);

    List<ConstructionElementResponseDto> getByRoom(Long roomId);

    ConstructionElementResponseDto getById(Long id);

    ConstructionElementResponseDto updateConstructionElement(Long id, ConstructionElementRequestDto request);

    void deleteConstructionElement(Long id);
}

