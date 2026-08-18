package com.acdiorr.buildpilot.controller;

import com.acdiorr.buildpilot.dto.ConstructionTemplateRequestDto;
import com.acdiorr.buildpilot.dto.ConstructionTemplateResponseDto;
import com.acdiorr.buildpilot.service.ConstructionTemplateService;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/construction-templates")
@RequiredArgsConstructor
@Tag(name = "Construction Templates", description = "CRUD operations for reusable construction templates")
public class ConstructionTemplateController {

    private final ConstructionTemplateService constructionTemplateService;

    @Operation(summary = "List all construction templates")
    @ApiResponse(responseCode = "200", description = "Construction templates retrieved")
    @GetMapping
    public ResponseEntity<List<ConstructionTemplateResponseDto>> getAllConstructionTemplates() {
        log.info("GET /api/v1/construction-templates");
        return ResponseEntity.ok(constructionTemplateService.getAllConstructionTemplates());
    }

    @Operation(summary = "Get a construction template by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Construction template found"),
            @ApiResponse(responseCode = "404", description = "Construction template not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ConstructionTemplateResponseDto> getConstructionTemplateById(@PathVariable Long id) {
        log.info("GET /api/v1/construction-templates/{}", id);
        return ResponseEntity.ok(constructionTemplateService.getConstructionTemplateById(id));
    }

    @Operation(summary = "Create a new construction template")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Construction template created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "409", description = "Construction template name already exists")
    })
    @PostMapping
    public ResponseEntity<ConstructionTemplateResponseDto> createConstructionTemplate(
            @Valid @RequestBody ConstructionTemplateRequestDto request) {

        log.info("POST /api/v1/construction-templates - name: {}", request.getName());
        ConstructionTemplateResponseDto created = constructionTemplateService.createConstructionTemplate(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Update an existing construction template")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Construction template updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Construction template not found"),
            @ApiResponse(responseCode = "409", description = "Construction template name already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ConstructionTemplateResponseDto> updateConstructionTemplate(
            @PathVariable Long id,
            @Valid @RequestBody ConstructionTemplateRequestDto request) {

        log.info("PUT /api/v1/construction-templates/{}", id);
        return ResponseEntity.ok(constructionTemplateService.updateConstructionTemplate(id, request));
    }

    @Operation(summary = "Delete a construction template")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Construction template deleted"),
            @ApiResponse(responseCode = "404", description = "Construction template not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConstructionTemplate(@PathVariable Long id) {
        log.info("DELETE /api/v1/construction-templates/{}", id);
        constructionTemplateService.deleteConstructionTemplate(id);
        return ResponseEntity.noContent().build();
    }
}

