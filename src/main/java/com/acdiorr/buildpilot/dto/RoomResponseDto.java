package com.acdiorr.buildpilot.dto;

import com.acdiorr.buildpilot.entity.enums.RoomType;
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
    private Integer doorCount;
    private Integer windowCount;
    private String observations;

    // Project summary — avoids exposing the full Project entity
    private Long projectId;
    private String projectName;
}

