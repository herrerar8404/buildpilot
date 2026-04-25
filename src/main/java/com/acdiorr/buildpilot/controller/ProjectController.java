package com.acdiorr.buildpilot.controller;

import com.acdiorr.buildpilot.dto.ProjectRequestDto;
import com.acdiorr.buildpilot.dto.ProjectResponseDto;
import com.acdiorr.buildpilot.service.ProjectService;
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
 * REST controller for Project CRUD operations.
 * Base path: /api/v1/projects
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "CRUD operations for construction projects")
public class ProjectController {

    private final ProjectService projectService;

    // ─── GET /api/v1/projects ────────────────────────────────────────────────
    @Operation(summary = "List all projects")
    @ApiResponse(responseCode = "200", description = "Projects retrieved successfully")
    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> getAllProjects() {
        log.info("GET /api/v1/projects");
        return ResponseEntity.ok(projectService.findAll());
    }

    // ─── GET /api/v1/projects/{id} ───────────────────────────────────────────
    @Operation(summary = "Get a project by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project found"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable Long id) {
        log.info("GET /api/v1/projects/{}", id);
        return ResponseEntity.ok(projectService.findById(id));
    }

    // ─── POST /api/v1/projects ───────────────────────────────────────────────
    @Operation(summary = "Create a new project")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Project created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(
            @Valid @RequestBody ProjectRequestDto request) {

        log.info("POST /api/v1/projects - payload: {}", request.getProjectName());
        ProjectResponseDto created = projectService.createProject(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    // ─── PUT /api/v1/projects/{id} ───────────────────────────────────────────
    @Operation(summary = "Update an existing project")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project updated"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequestDto request) {

        log.info("PUT /api/v1/projects/{}", id);
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    // ─── DELETE /api/v1/projects/{id} ────────────────────────────────────────
    @Operation(summary = "Delete a project")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Project deleted"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        log.info("DELETE /api/v1/projects/{}", id);
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}

