package com.acdiorr.buildpilot.controller;

import com.acdiorr.buildpilot.dto.MaterialRequestDto;
import com.acdiorr.buildpilot.dto.MaterialResponseDto;
import com.acdiorr.buildpilot.service.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
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
 * REST controller for Material catalogue CRUD operations.
 * Base path: /api/v1/materials
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/materials")
@RequiredArgsConstructor
@Tag(name = "Materials", description = "Global material catalogue — prices and units")
public class MaterialController {

    private final MaterialService materialService;

    // ─── GET /api/v1/materials ────────────────────────────────────────────────
    @Operation(summary = "List all materials")
    @ApiResponse(responseCode = "200", description = "Materials retrieved successfully")
    @GetMapping
    public ResponseEntity<List<MaterialResponseDto>> getAllMaterials() {
        log.info("GET /api/v1/materials");
        return ResponseEntity.ok(materialService.getAllMaterials());
    }

    // ─── GET /api/v1/materials/{id} ───────────────────────────────────────────
    @Operation(summary = "Get a material by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Material found"),
            @ApiResponse(responseCode = "404", description = "Material not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponseDto> getMaterialById(@PathVariable Long id) {
        log.info("GET /api/v1/materials/{}", id);
        return ResponseEntity.ok(materialService.getMaterialById(id));
    }

    // ─── POST /api/v1/materials ───────────────────────────────────────────────
    @Operation(summary = "Create a new material")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Material created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or duplicate name"),
            @ApiResponse(responseCode = "409", description = "Material name already exists")
    })
    @PostMapping
    public ResponseEntity<MaterialResponseDto> createMaterial(
            @Valid @RequestBody MaterialRequestDto request) {

        log.info("POST /api/v1/materials - name: {}", request.getName());
        MaterialResponseDto created = materialService.createMaterial(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    // ─── PUT /api/v1/materials/{id} ───────────────────────────────────────────
    @Operation(summary = "Update an existing material")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Material updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Material not found"),
            @ApiResponse(responseCode = "409", description = "Material name already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponseDto> updateMaterial(
            @PathVariable Long id,
            @Valid @RequestBody MaterialRequestDto request) {

        log.info("PUT /api/v1/materials/{}", id);
        return ResponseEntity.ok(materialService.updateMaterial(id, request));
    }

    // ─── DELETE /api/v1/materials/{id} ────────────────────────────────────────
    @Operation(summary = "Delete a material")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Material deleted"),
            @ApiResponse(responseCode = "404", description = "Material not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable Long id) {
        log.info("DELETE /api/v1/materials/{}", id);
        materialService.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }
}

