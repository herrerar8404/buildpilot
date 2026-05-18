package com.acdiorr.buildpilot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Specialized response contract for floor plan preview rendering.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloorPlanResponseDto {

    private Long projectId;
    private String projectName;

    @Schema(description = "Project land width used to scale the floor plan canvas", example = "20.00")
    private BigDecimal landWidth;

    @Schema(description = "Project land length used to scale the floor plan canvas", example = "30.00")
    private BigDecimal landLength;

    private List<FloorPlanRoomDto> rooms;
}

