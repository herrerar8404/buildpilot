package com.acdiorr.buildpilot.dto;

import com.acdiorr.buildpilot.entity.enums.RoomShapeType;
import com.acdiorr.buildpilot.entity.enums.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO used for CREATE and UPDATE room requests (POST / PUT).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequestDto {

    @NotBlank(message = "Room name is required")
    private String name;

    private RoomType roomType;

    @Schema(description = "Room geometric shape type", example = "RECTANGLE")
    private RoomShapeType shapeType;

    @Schema(description = "Number of effective walls for the room", example = "4")
    @Min(value = 2, message = "wallCount must be greater than or equal to 2")
    private Integer wallCount;

    private BigDecimal width;

    private BigDecimal length;

    private BigDecimal height;

    @Schema(description = "X coordinate used for 2D floor plan rendering", example = "12.50")
    @DecimalMin(value = "0.0", inclusive = true, message = "positionX must be greater than or equal to 0")
    private BigDecimal positionX;

    @Schema(description = "Y coordinate used for 2D floor plan rendering", example = "8.75")
    @DecimalMin(value = "0.0", inclusive = true, message = "positionY must be greater than or equal to 0")
    private BigDecimal positionY;

    private Integer doorCount;

    private Integer windowCount;

    private String observations;

    @AssertTrue(message = "wallCount is not valid for the selected shapeType")
    public boolean isWallCountValidForShape() {
        RoomShapeType effectiveShape = shapeType == null ? RoomShapeType.RECTANGLE : shapeType;
        int effectiveWallCount = wallCount == null
                ? (effectiveShape == RoomShapeType.RECTANGLE ? 4 : 3)
                : wallCount;

        if (effectiveShape == RoomShapeType.OPEN_AREA) {
            return effectiveWallCount >= 2;
        }
        return effectiveWallCount >= 3;
    }
}
