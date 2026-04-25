package com.acdiorr.buildpilot.dto;

import com.acdiorr.buildpilot.entity.enums.RoomType;
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

    private BigDecimal width;

    private BigDecimal length;

    private BigDecimal height;

    private Integer doorCount;

    private Integer windowCount;

    private String observations;
}

