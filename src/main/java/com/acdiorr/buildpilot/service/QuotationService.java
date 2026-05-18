package com.acdiorr.buildpilot.service;

import com.acdiorr.buildpilot.dto.QuotationRequestDto;
import com.acdiorr.buildpilot.dto.QuotationResponseDto;

public interface QuotationService {

    QuotationResponseDto createQuotation(Long projectId, QuotationRequestDto request);

    QuotationResponseDto getByProject(Long projectId);

    QuotationResponseDto getById(Long id);

    QuotationResponseDto updateQuotation(Long id, QuotationRequestDto request);

    void deleteQuotation(Long id);
}

