package com.acdiorr.buildpilot.service.impl;

import com.acdiorr.buildpilot.dto.QuotationRequestDto;
import com.acdiorr.buildpilot.dto.QuotationResponseDto;
import com.acdiorr.buildpilot.entity.Project;
import com.acdiorr.buildpilot.entity.Quotation;
import com.acdiorr.buildpilot.exception.DuplicateQuotationException;
import com.acdiorr.buildpilot.exception.ProjectNotFoundException;
import com.acdiorr.buildpilot.exception.QuotationNotFoundException;
import com.acdiorr.buildpilot.mapper.QuotationMapper;
import com.acdiorr.buildpilot.repository.ElementMaterialRepository;
import com.acdiorr.buildpilot.repository.ProjectRepository;
import com.acdiorr.buildpilot.repository.QuotationRepository;
import com.acdiorr.buildpilot.service.QuotationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuotationServiceImpl implements QuotationService {

    private static final int MONEY_SCALE = 2;
    private static final int PERCENT_DIVISION_SCALE = 6;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private final QuotationRepository quotationRepository;
    private final ProjectRepository projectRepository;
    private final ElementMaterialRepository elementMaterialRepository;
    private final QuotationMapper quotationMapper;

    @Override
    @Transactional
    public QuotationResponseDto createQuotation(Long projectId, QuotationRequestDto request) {
        log.info("Creating quotation for project id: {}", projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        if (quotationRepository.existsByProjectId(projectId)) {
            throw new DuplicateQuotationException(projectId);
        }

        Quotation quotation = quotationMapper.toEntity(request, project);
        applyFinancialCalculations(quotation, projectId);

        Quotation saved = quotationRepository.save(quotation);
        return quotationMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public QuotationResponseDto getByProject(Long projectId) {
        log.info("Fetching quotation for project id: {}", projectId);

        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException(projectId);
        }

        return quotationRepository.findByProjectId(projectId)
                .map(quotationMapper::toResponseDto)
                .orElseThrow(() -> new QuotationNotFoundException("Quotation not found for project id: " + projectId));
    }

    @Override
    @Transactional(readOnly = true)
    public QuotationResponseDto getById(Long id) {
        log.info("Fetching quotation id: {}", id);
        return quotationRepository.findById(id)
                .map(quotationMapper::toResponseDto)
                .orElseThrow(() -> new QuotationNotFoundException(id));
    }

    @Override
    @Transactional
    public QuotationResponseDto updateQuotation(Long id, QuotationRequestDto request) {
        log.info("Updating quotation id: {}", id);
        Quotation existing = quotationRepository.findById(id)
                .orElseThrow(() -> new QuotationNotFoundException(id));

        quotationMapper.updateEntityFromDto(request, existing);
        applyFinancialCalculations(existing, existing.getProject().getId());

        Quotation saved = quotationRepository.save(existing);
        return quotationMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public Optional<QuotationResponseDto> recalculateProjectQuotationIfExists(Long projectId) {
        log.info("Recalculating quotation for project id={} if it exists", projectId);

        return quotationRepository.findByProjectId(projectId)
                .map(quotation -> {
                    applyFinancialCalculations(quotation, projectId);
                    Quotation saved = quotationRepository.save(quotation);
                    return quotationMapper.toResponseDto(saved);
                });
    }

    @Override
    @Transactional
    public void deleteQuotation(Long id) {
        log.info("Deleting quotation id: {}", id);
        if (!quotationRepository.existsById(id)) {
            throw new QuotationNotFoundException(id);
        }
        quotationRepository.deleteById(id);
    }

    private void applyFinancialCalculations(Quotation quotation, Long projectId) {
        BigDecimal materialCost = elementMaterialRepository.sumCalculatedCostByProjectId(projectId);
        if (materialCost == null) {
            materialCost = BigDecimal.ZERO;
        }
        materialCost = materialCost.setScale(MONEY_SCALE, ROUNDING);

        BigDecimal laborCost = normalizeMoney(quotation.getLaborCost());
        BigDecimal indirectCost = normalizeMoney(quotation.getIndirectCost());
        BigDecimal profitMargin = quotation.getProfitMargin() == null
                ? BigDecimal.ZERO
                : quotation.getProfitMargin();

        BigDecimal subtotal = materialCost
                .add(laborCost)
                .add(indirectCost)
                .setScale(MONEY_SCALE, ROUNDING);

        BigDecimal profitAmount = subtotal
                .multiply(profitMargin)
                .divide(BigDecimal.valueOf(100), PERCENT_DIVISION_SCALE, ROUNDING)
                .setScale(MONEY_SCALE, ROUNDING);

        BigDecimal totalCost = subtotal
                .add(profitAmount)
                .setScale(MONEY_SCALE, ROUNDING);

        quotation.setMaterialCost(materialCost);
        quotation.setLaborCost(laborCost);
        quotation.setIndirectCost(indirectCost);
        quotation.setProfitMargin(profitMargin.setScale(2, ROUNDING));
        quotation.setSubtotal(subtotal);
        quotation.setTotalCost(totalCost);
    }

    private BigDecimal normalizeMoney(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE, ROUNDING);
        }
        return value.setScale(MONEY_SCALE, ROUNDING);
    }
}

