package com.acdiorr.buildpilot.controller;

import com.acdiorr.buildpilot.dto.TemplateMaterialRequestDto;
import com.acdiorr.buildpilot.dto.TemplateMaterialResponseDto;
import com.acdiorr.buildpilot.service.TemplateMaterialService;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Template Materials", description = "Assign materials to construction templates")
public class TemplateMaterialController {

    private final TemplateMaterialService templateMaterialService;

    @Operation(summary = "Assign a material to a construction template")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Template material created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Construction template or material not found"),
            @ApiResponse(responseCode = "409", description = "Material already assigned to this construction template")
    })
    @PostMapping("/api/v1/construction-templates/{constructionTemplateId}/materials/{materialId}")
    public ResponseEntity<TemplateMaterialResponseDto> createTemplateMaterial(
            @Parameter(description = "Construction template ID", required = true)
            @PathVariable Long constructionTemplateId,
            @Parameter(description = "Material ID", required = true)
            @PathVariable Long materialId,
            @Valid @RequestBody TemplateMaterialRequestDto request) {

        log.info("POST /api/v1/construction-templates/{}/materials/{}", constructionTemplateId, materialId);

        TemplateMaterialResponseDto created = templateMaterialService.createTemplateMaterial(
                constructionTemplateId,
                materialId,
                request
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/template-materials/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "List all materials assigned to a construction template")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Template materials retrieved"),
            @ApiResponse(responseCode = "404", description = "Construction template not found")
    })
    @GetMapping("/api/v1/construction-templates/{constructionTemplateId}/materials")
    public ResponseEntity<List<TemplateMaterialResponseDto>> getByConstructionTemplate(
            @Parameter(description = "Construction template ID", required = true)
            @PathVariable Long constructionTemplateId) {

        log.info("GET /api/v1/construction-templates/{}/materials", constructionTemplateId);
        return ResponseEntity.ok(templateMaterialService.getByConstructionTemplate(constructionTemplateId));
    }

    @Operation(summary = "List all template materials")
    @ApiResponse(responseCode = "200", description = "Template materials retrieved")
    @GetMapping("/api/v1/template-materials")
    public ResponseEntity<List<TemplateMaterialResponseDto>> getAllTemplateMaterials() {
        log.info("GET /api/v1/template-materials");
        return ResponseEntity.ok(templateMaterialService.getAllTemplateMaterials());
    }

    @Operation(summary = "Get template material by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Template material found"),
            @ApiResponse(responseCode = "404", description = "Template material not found")
    })
    @GetMapping("/api/v1/template-materials/{id}")
    public ResponseEntity<TemplateMaterialResponseDto> getById(
            @Parameter(description = "Template material ID", required = true)
            @PathVariable Long id) {

        log.info("GET /api/v1/template-materials/{}", id);
        return ResponseEntity.ok(templateMaterialService.getById(id));
    }

    @Operation(summary = "Update a template material")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Template material updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Template material not found")
    })
    @PutMapping("/api/v1/template-materials/{id}")
    public ResponseEntity<TemplateMaterialResponseDto> updateTemplateMaterial(
            @Parameter(description = "Template material ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody TemplateMaterialRequestDto request) {

        log.info("PUT /api/v1/template-materials/{}", id);
        return ResponseEntity.ok(templateMaterialService.updateTemplateMaterial(id, request));
    }

    @Operation(summary = "Delete a template material")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Template material deleted"),
            @ApiResponse(responseCode = "404", description = "Template material not found")
    })
    @DeleteMapping("/api/v1/template-materials/{id}")
    public ResponseEntity<Void> deleteTemplateMaterial(
            @Parameter(description = "Template material ID", required = true)
            @PathVariable Long id) {

        log.info("DELETE /api/v1/template-materials/{}", id);
        templateMaterialService.deleteTemplateMaterial(id);
        return ResponseEntity.noContent().build();
    }
}

