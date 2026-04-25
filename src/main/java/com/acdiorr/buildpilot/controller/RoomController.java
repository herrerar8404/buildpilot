package com.acdiorr.buildpilot.controller;

import com.acdiorr.buildpilot.dto.RoomRequestDto;
import com.acdiorr.buildpilot.dto.RoomResponseDto;
import com.acdiorr.buildpilot.service.RoomService;
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
 * REST controller for Room CRUD operations.
 *
 * Nested under project:
 *   POST   /api/v1/projects/{projectId}/rooms
 *   GET    /api/v1/projects/{projectId}/rooms
 *
 * Standalone by room id:
 *   GET    /api/v1/rooms/{id}
 *   PUT    /api/v1/rooms/{id}
 *   DELETE /api/v1/rooms/{id}
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Rooms", description = "CRUD operations for rooms within a project")
public class RoomController {

    private final RoomService roomService;

    // ─── POST /api/v1/projects/{projectId}/rooms ─────────────────────────────
    @Operation(summary = "Create a room inside a project")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Room created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @PostMapping("/api/v1/projects/{projectId}/rooms")
    public ResponseEntity<RoomResponseDto> createRoom(
            @PathVariable Long projectId,
            @Valid @RequestBody RoomRequestDto request) {

        log.info("POST /api/v1/projects/{}/rooms", projectId);
        RoomResponseDto created = roomService.createRoom(projectId, request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/rooms/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    // ─── GET /api/v1/projects/{projectId}/rooms ───────────────────────────────
    @Operation(summary = "List all rooms of a project")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rooms retrieved"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @GetMapping("/api/v1/projects/{projectId}/rooms")
    public ResponseEntity<List<RoomResponseDto>> getRoomsByProject(
            @PathVariable Long projectId) {

        log.info("GET /api/v1/projects/{}/rooms", projectId);
        return ResponseEntity.ok(roomService.findAllByProject(projectId));
    }

    // ─── GET /api/v1/rooms/{id} ───────────────────────────────────────────────
    @Operation(summary = "Get a room by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room found"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @GetMapping("/api/v1/rooms/{id}")
    public ResponseEntity<RoomResponseDto> getRoomById(@PathVariable Long id) {
        log.info("GET /api/v1/rooms/{}", id);
        return ResponseEntity.ok(roomService.findById(id));
    }

    // ─── PUT /api/v1/rooms/{id} ───────────────────────────────────────────────
    @Operation(summary = "Update a room")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room updated"),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PutMapping("/api/v1/rooms/{id}")
    public ResponseEntity<RoomResponseDto> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequestDto request) {

        log.info("PUT /api/v1/rooms/{}", id);
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    // ─── DELETE /api/v1/rooms/{id} ────────────────────────────────────────────
    @Operation(summary = "Delete a room")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Room deleted"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    @DeleteMapping("/api/v1/rooms/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        log.info("DELETE /api/v1/rooms/{}", id);
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}

