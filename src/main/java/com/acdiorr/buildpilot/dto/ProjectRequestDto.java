package com.acdiorr.buildpilot.dto;

import com.acdiorr.buildpilot.entity.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO used for CREATE and UPDATE requests (POST / PUT).
 * Keeps the JPA entity out of the API layer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDto {

    @NotBlank(message = "Project name is required")
    private String projectName;

    @NotBlank(message = "Client name is required")
    private String clientName;

    private String constructionType;

    private BigDecimal landWidth;

    private BigDecimal landLength;

    private String address;

    private String description;

    private LocalDate creationDate;

    @NotNull(message = "Project status is required")
    private ProjectStatus status;
}

