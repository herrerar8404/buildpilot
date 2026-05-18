package com.acdiorr.buildpilot.controller;

import com.acdiorr.buildpilot.dto.ElementMaterialRequestDto;
import com.acdiorr.buildpilot.dto.ElementMaterialResponseDto;
import com.acdiorr.buildpilot.service.ElementMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST controller for ElementMaterial operations.
 * <p>
 * Nested resource paths follow the "construction-elements → materials" hierarchy.
 * Standalone look-up / update / delete paths use /api/v1/element-materials/{id}.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Element Materials",
     description = "Assign materials to construction elements and manage quantity / cost calculations")
public class ElementMaterialController {

    private final ElementMaterialService elementMaterialService;

    // ─── POST /api/v1/construction-elements/{constructionElementId}/materials/{materialId} ──

    @Operation(
        summary = "Assign a material to a construction element",
        description = "Creates an ElementMaterial record. " +
                      "finalQuantity and calculatedCost are always computed server-side.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "ElementMaterial created"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "404", description = "Construction element or material not found"),
        @ApiResponse(responseCode = "409", description = "Material already assigned to this construction element")
    })
    @PostMapping("/api/v1/construction-elements/{constructionElementId}/materials/{materialId}")
    public ResponseEntity<ElementMaterialResponseDto> createElementMaterial(
            @Parameter(description = "ID of the construction element", required = true)
            @PathVariable Long constructionElementId,
            @Parameter(description = "ID of the material to assign", required = true)
            @PathVariable Long materialId,
            @Valid @RequestBody ElementMaterialRequestDto request) {

        log.info("POST /api/v1/construction-elements/{}/materials/{}", constructionElementId, materialId);

        ElementMaterialResponseDto created =
                elementMaterialService.createElementMaterial(constructionElementId, materialId, request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/element-materials/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    // ─── GET /api/v1/construction-elements/{constructionElementId}/materials ─────────────

    @Operation(
        summary = "List all materials assigned to a construction element",
        description = "Returns every ElementMaterial that belongs to the given construction element, " +
                      "including pre-computed finalQuantity and calculatedCost.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List returned (may be empty)"),
        @ApiResponse(responseCode = "404", description = "Construction element not found")
    })
    @GetMapping("/api/v1/construction-elements/{constructionElementId}/materials")
    public ResponseEntity<List<ElementMaterialResponseDto>> getByConstructionElement(
            @Parameter(description = "ID of the construction element", required = true)
            @PathVariable Long constructionElementId) {

        log.info("GET /api/v1/construction-elements/{}/materials", constructionElementId);
        return ResponseEntity.ok(elementMaterialService.getByConstructionElement(constructionElementId));
    }

    // ─── GET /api/v1/element-materials/{id} ─────────────────────────────────────────────

    @Operation(summary = "Get an ElementMaterial by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "ElementMaterial found"),
        @ApiResponse(responseCode = "404", description = "ElementMaterial not found")
    })
    @GetMapping("/api/v1/element-materials/{id}")
    public ResponseEntity<ElementMaterialResponseDto> getById(
            @Parameter(description = "ElementMaterial ID", required = true)
            @PathVariable Long id) {

        log.info("GET /api/v1/element-materials/{}", id);
        return ResponseEntity.ok(elementMaterialService.getById(id));
    }

    // ─── PUT /api/v1/element-materials/{id} ─────────────────────────────────────────────

    @Operation(
        summary = "Update an ElementMaterial",
        description = "Updates input fields (requiredQuantity, wastePercentage, etc.) " +
                      "and recalculates finalQuantity and calculatedCost server-side. " +
                      "The element–material pair cannot be changed via this endpoint.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "ElementMaterial updated"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "404", description = "ElementMaterial not found")
    })
    @PutMapping("/api/v1/element-materials/{id}")
    public ResponseEntity<ElementMaterialResponseDto> updateElementMaterial(
            @Parameter(description = "ElementMaterial ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ElementMaterialRequestDto request) {

        log.info("PUT /api/v1/element-materials/{}", id);
        return ResponseEntity.ok(elementMaterialService.updateElementMaterial(id, request));
    }

    // ─── DELETE /api/v1/element-materials/{id} ──────────────────────────────────────────

    @Operation(summary = "Delete an ElementMaterial")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "ElementMaterial deleted"),
        @ApiResponse(responseCode = "404", description = "ElementMaterial not found")
    })
    @DeleteMapping("/api/v1/element-materials/{id}")
    public ResponseEntity<Void> deleteElementMaterial(
            @Parameter(description = "ElementMaterial ID", required = true)
            @PathVariable Long id) {

        log.info("DELETE /api/v1/element-materials/{}", id);
        elementMaterialService.deleteElementMaterial(id);
        return ResponseEntity.noContent().build();
    }
}

