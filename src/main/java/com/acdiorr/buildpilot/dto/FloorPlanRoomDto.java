package com.acdiorr.buildpilot.dto;

import com.acdiorr.buildpilot.entity.enums.RoomShapeType;
import com.acdiorr.buildpilot.entity.enums.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Geometry-focused DTO used to render a room in a 2D floor plan preview.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloorPlanRoomDto {

    private Long id;
    private String name;
    private RoomType roomType;

    @Schema(description = "Room geometric shape type", example = "RECTANGLE")
    private RoomShapeType shapeType;

    @Schema(description = "Number of effective walls for the room", example = "4")
    private Integer wallCount;

    @Schema(description = "Room width used by the frontend renderer", example = "4.50")
    private BigDecimal width;

    @Schema(description = "Room length used by the frontend renderer", example = "5.20")
    private BigDecimal length;

    @Schema(description = "X coordinate inside the project floor plan", example = "10.00")
    private BigDecimal positionX;

    @Schema(description = "Y coordinate inside the project floor plan", example = "8.00")
    private BigDecimal positionY;
}
