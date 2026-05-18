package com.acdiorr.buildpilot.controller;

import com.acdiorr.buildpilot.dto.FloorPlanResponseDto;
import com.acdiorr.buildpilot.service.FloorPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Floor Plan", description = "Project floor plan preview geometry for SVG/Canvas rendering")
public class FloorPlanController {

    private final FloorPlanService floorPlanService;

    @Operation(summary = "Get project floor plan preview")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Floor plan retrieved"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @GetMapping("/api/v1/projects/{projectId}/floor-plan")
    public ResponseEntity<FloorPlanResponseDto> getFloorPlan(@PathVariable Long projectId) {
        log.info("GET /api/v1/projects/{}/floor-plan", projectId);
        return ResponseEntity.ok(floorPlanService.getFloorPlan(projectId));
    }
}

