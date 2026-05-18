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
 * DTO used for all room API responses.
 * Never exposes the JPA entity or its bidirectional relationship directly.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponseDto {

    private Long id;
    private String name;
    private RoomType roomType;
    private BigDecimal width;
    private BigDecimal length;
    private BigDecimal height;

    @Schema(description = "Room geometric shape type", example = "RECTANGLE")
    private RoomShapeType shapeType;

    @Schema(description = "Number of effective walls for the room", example = "4")
    private Integer wallCount;

    @Schema(description = "X coordinate used for 2D floor plan rendering", example = "12.50")
    private BigDecimal positionX;

    @Schema(description = "Y coordinate used for 2D floor plan rendering", example = "8.75")
    private BigDecimal positionY;

    private Integer doorCount;
    private Integer windowCount;
    private String observations;

    // Project summary — avoids exposing the full Project entity
    private Long projectId;
    private String projectName;
}
