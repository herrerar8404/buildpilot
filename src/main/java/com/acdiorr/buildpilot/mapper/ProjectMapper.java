package com.acdiorr.buildpilot.mapper;

import com.acdiorr.buildpilot.dto.ProjectRequestDto;
import com.acdiorr.buildpilot.dto.ProjectResponseDto;
import com.acdiorr.buildpilot.entity.Project;
import org.springframework.stereotype.Component;

/**
 * Maps between Project entity and DTOs.
 * A dedicated mapper keeps the controller and service clean.
 */
@Component
public class ProjectMapper {

    public Project toEntity(ProjectRequestDto dto) {
        return Project.builder()
                .projectName(dto.getProjectName())
                .clientName(dto.getClientName())
                .constructionType(dto.getConstructionType())
                .landWidth(dto.getLandWidth())
                .landLength(dto.getLandLength())
                .address(dto.getAddress())
                .description(dto.getDescription())
                .creationDate(dto.getCreationDate())
                .status(dto.getStatus())
                .build();
    }

    public ProjectResponseDto toResponseDto(Project project) {
        return ProjectResponseDto.builder()
                .id(project.getId())
                .projectName(project.getProjectName())
                .clientName(project.getClientName())
                .constructionType(project.getConstructionType())
                .landWidth(project.getLandWidth())
                .landLength(project.getLandLength())
                .address(project.getAddress())
                .description(project.getDescription())
                .creationDate(project.getCreationDate())
                .status(project.getStatus())
                .roomCount(project.getRooms() != null ? project.getRooms().size() : 0)
                .build();
    }

    public void updateEntityFromDto(ProjectRequestDto dto, Project existing) {
        existing.setProjectName(dto.getProjectName());
        existing.setClientName(dto.getClientName());
        existing.setConstructionType(dto.getConstructionType());
        existing.setLandWidth(dto.getLandWidth());
        existing.setLandLength(dto.getLandLength());
        existing.setAddress(dto.getAddress());
        existing.setDescription(dto.getDescription());
        existing.setStatus(dto.getStatus());
    }
}

