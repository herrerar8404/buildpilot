package com.acdiorr.buildpilot.service;

import com.acdiorr.buildpilot.dto.ElementMaterialRequestDto;
import com.acdiorr.buildpilot.dto.ElementMaterialResponseDto;

import java.util.List;

public interface ElementMaterialService {

    /**
     * Assign a material to a construction element with the given quantity and waste data.
     * {@code finalQuantity} and {@code calculatedCost} are computed server-side.
     *
     * @param constructionElementId ID of the parent ConstructionElement
     * @param materialId            ID of the Material to assign
     * @param request               input fields (requiredQuantity, waste, etc.)
     * @return persisted ElementMaterial as a response DTO
     */
    ElementMaterialResponseDto createElementMaterial(Long constructionElementId,
                                                     Long materialId,
                                                     ElementMaterialRequestDto request);

    /**
     * Returns all materials assigned to the given construction element.
     */
    List<ElementMaterialResponseDto> getByConstructionElement(Long constructionElementId);

    /**
     * Returns a single ElementMaterial by its own primary key.
     */
    ElementMaterialResponseDto getById(Long id);

    /**
     * Updates the input fields and recalculates the computed fields.
     * The element–material pair (foreign keys) cannot be changed via this operation.
     */
    ElementMaterialResponseDto updateElementMaterial(Long id, ElementMaterialRequestDto request);

    /**
     * Removes an ElementMaterial record permanently.
     */
    void deleteElementMaterial(Long id);
}

