package com.acdiorr.buildpilot.controller;

import com.acdiorr.buildpilot.dto.ConstructionElementRequestDto;
import com.acdiorr.buildpilot.dto.ConstructionElementResponseDto;
import com.acdiorr.buildpilot.service.ConstructionElementService;
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
 * REST controller for ConstructionElement operations.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Construction Elements", description = "Manage construction elements inside a room")
public class ConstructionElementController {

    private final ConstructionElementService constructionElementService;

    @Operation(summary = "Create a construction element inside a room")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Construction element created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @PostMapping("/api/v1/rooms/{roomId}/construction-elements")
    public ResponseEntity<ConstructionElementResponseDto> createConstructionElement(
            @PathVariable Long roomId,
            @Valid @RequestBody ConstructionElementRequestDto request) {

        log.info("POST /api/v1/rooms/{}/construction-elements", roomId);
        ConstructionElementResponseDto created = constructionElementService.createConstructionElement(roomId, request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/construction-elements/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "List all construction elements of a room")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Construction elements retrieved"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @GetMapping("/api/v1/rooms/{roomId}/construction-elements")
    public ResponseEntity<List<ConstructionElementResponseDto>> getByRoom(@PathVariable Long roomId) {
        log.info("GET /api/v1/rooms/{}/construction-elements", roomId);
        return ResponseEntity.ok(constructionElementService.getByRoom(roomId));
    }

    @Operation(summary = "Get a construction element by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Construction element found"),
            @ApiResponse(responseCode = "404", description = "Construction element not found")
    })
    @GetMapping("/api/v1/construction-elements/{id}")
    public ResponseEntity<ConstructionElementResponseDto> getById(@PathVariable Long id) {
        log.info("GET /api/v1/construction-elements/{}", id);
        return ResponseEntity.ok(constructionElementService.getById(id));
    }

    @Operation(summary = "Update a construction element")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Construction element updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Construction element not found")
    })
    @PutMapping("/api/v1/construction-elements/{id}")
    public ResponseEntity<ConstructionElementResponseDto> updateConstructionElement(
            @PathVariable Long id,
            @Valid @RequestBody ConstructionElementRequestDto request) {

        log.info("PUT /api/v1/construction-elements/{}", id);
        return ResponseEntity.ok(constructionElementService.updateConstructionElement(id, request));
    }

    @Operation(summary = "Delete a construction element")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Construction element deleted"),
            @ApiResponse(responseCode = "404", description = "Construction element not found")
    })
    @DeleteMapping("/api/v1/construction-elements/{id}")
    public ResponseEntity<Void> deleteConstructionElement(@PathVariable Long id) {
        log.info("DELETE /api/v1/construction-elements/{}", id);
        constructionElementService.deleteConstructionElement(id);
        return ResponseEntity.noContent().build();
    }
}

