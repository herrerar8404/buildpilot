package com.acdiorr.buildpilot.service.impl;

import com.acdiorr.buildpilot.dto.ElementMaterialRequestDto;
import com.acdiorr.buildpilot.dto.ElementMaterialResponseDto;
import com.acdiorr.buildpilot.entity.ConstructionElement;
import com.acdiorr.buildpilot.entity.ElementMaterial;
import com.acdiorr.buildpilot.entity.Material;
import com.acdiorr.buildpilot.exception.ConstructionElementNotFoundException;
import com.acdiorr.buildpilot.exception.DuplicateElementMaterialException;
import com.acdiorr.buildpilot.exception.ElementMaterialNotFoundException;
import com.acdiorr.buildpilot.exception.MaterialNotFoundException;
import com.acdiorr.buildpilot.mapper.ElementMaterialMapper;
import com.acdiorr.buildpilot.repository.ConstructionElementRepository;
import com.acdiorr.buildpilot.repository.ElementMaterialRepository;
import com.acdiorr.buildpilot.repository.MaterialRepository;
import com.acdiorr.buildpilot.service.ElementMaterialService;
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
public class ElementMaterialServiceImpl implements ElementMaterialService {

    private static final int QUANTITY_SCALE = 4;
    private static final int COST_SCALE     = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private final ElementMaterialRepository elementMaterialRepository;
    private final ConstructionElementRepository constructionElementRepository;
    private final MaterialRepository materialRepository;
    private final ElementMaterialMapper elementMaterialMapper;

    // ─── Create ──────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ElementMaterialResponseDto createElementMaterial(Long constructionElementId,
                                                            Long materialId,
                                                            ElementMaterialRequestDto request) {
        log.info("Assigning material id={} to construction element id={}", materialId, constructionElementId);

        ConstructionElement element = resolveConstructionElement(constructionElementId);
        Material material           = resolveMaterial(materialId);

        if (elementMaterialRepository.existsByConstructionElementIdAndMaterialId(
                constructionElementId, materialId)) {
            throw new DuplicateElementMaterialException(constructionElementId, materialId);
        }

        ElementMaterial em = elementMaterialMapper.toEntity(request, element, material);
        applyCalculations(em, material);

        ElementMaterial saved = elementMaterialRepository.save(em);
        log.info("ElementMaterial created: id={}, finalQty={}, cost={}",
                saved.getId(), saved.getFinalQuantity(), saved.getCalculatedCost());
        return elementMaterialMapper.toResponseDto(saved);
    }

    // ─── Read ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<ElementMaterialResponseDto> getByConstructionElement(Long constructionElementId) {
        log.info("Fetching all materials for construction element id={}", constructionElementId);
        if (!constructionElementRepository.existsById(constructionElementId)) {
            throw new ConstructionElementNotFoundException(constructionElementId);
        }
        return elementMaterialRepository.findByConstructionElementId(constructionElementId)
                .stream()
                .map(elementMaterialMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ElementMaterialResponseDto getById(Long id) {
        log.info("Fetching ElementMaterial id={}", id);
        return elementMaterialRepository.findById(id)
                .map(elementMaterialMapper::toResponseDto)
                .orElseThrow(() -> new ElementMaterialNotFoundException(id));
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ElementMaterialResponseDto updateElementMaterial(Long id,
                                                            ElementMaterialRequestDto request) {
        log.info("Updating ElementMaterial id={}", id);
        ElementMaterial existing = elementMaterialRepository.findById(id)
                .orElseThrow(() -> new ElementMaterialNotFoundException(id));

        elementMaterialMapper.updateEntityFromDto(request, existing);
        applyCalculations(existing, existing.getMaterial());

        ElementMaterial saved = elementMaterialRepository.save(existing);
        log.info("ElementMaterial updated: id={}, finalQty={}, cost={}",
                saved.getId(), saved.getFinalQuantity(), saved.getCalculatedCost());
        return elementMaterialMapper.toResponseDto(saved);
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteElementMaterial(Long id) {
        log.info("Deleting ElementMaterial id={}", id);
        if (!elementMaterialRepository.existsById(id)) {
            throw new ElementMaterialNotFoundException(id);
        }
        elementMaterialRepository.deleteById(id);
    }

    // ─── Business calculations ────────────────────────────────────────────────

    /**
     * Applies the two core business formulas:
     * <pre>
     *   finalQuantity   = requiredQuantity × (1 + wastePercentage / 100)
     *   calculatedCost  = finalQuantity    × material.unitPrice
     * </pre>
     * Both results are rounded to their respective DB scales before persisting.
     */
    private void applyCalculations(ElementMaterial em, Material material) {
        BigDecimal waste = em.getWastePercentage() != null
                ? em.getWastePercentage()
                : BigDecimal.ZERO;

        // finalQuantity = requiredQuantity × (1 + waste / 100)
        BigDecimal wasteFactor = BigDecimal.ONE.add(
                waste.divide(BigDecimal.valueOf(100), QUANTITY_SCALE, ROUNDING));

        BigDecimal finalQty = em.getRequiredQuantity()
                .multiply(wasteFactor)
                .setScale(QUANTITY_SCALE, ROUNDING);

        // calculatedCost = finalQuantity × unitPrice
        BigDecimal cost = finalQty
                .multiply(material.getUnitPrice())
                .setScale(COST_SCALE, ROUNDING);

        em.setFinalQuantity(finalQty);
        em.setCalculatedCost(cost);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private ConstructionElement resolveConstructionElement(Long id) {
        return constructionElementRepository.findById(id)
                .orElseThrow(() -> new ConstructionElementNotFoundException(id));
    }

    private Material resolveMaterial(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new MaterialNotFoundException(id));
    }
}

