package com.acdiorr.buildpilot.service;

import com.acdiorr.buildpilot.dto.QuotationRequestDto;
import com.acdiorr.buildpilot.dto.QuotationResponseDto;

import java.util.Optional;

public interface QuotationService {

    QuotationResponseDto createQuotation(Long projectId, QuotationRequestDto request);

    QuotationResponseDto getByProject(Long projectId);

    QuotationResponseDto getById(Long id);

    QuotationResponseDto updateQuotation(Long id, QuotationRequestDto request);

    Optional<QuotationResponseDto> recalculateProjectQuotationIfExists(Long projectId);

    void deleteQuotation(Long id);
}

