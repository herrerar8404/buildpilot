package com.acdiorr.buildpilot.dto;

import com.acdiorr.buildpilot.entity.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO used for all API responses.
 * Exposes only the data the client needs — never the JPA entity directly.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDto {

    private Long id;
    private String projectName;
    private String clientName;
    private String constructionType;
    private BigDecimal landWidth;
    private BigDecimal landLength;
    private String address;
    private String description;
    private LocalDate creationDate;
    private ProjectStatus status;
    private int roomCount;
}

