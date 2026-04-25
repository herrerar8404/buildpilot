package com.acdiorr.buildpilot.service;

import com.acdiorr.buildpilot.dto.ProjectRequestDto;
import com.acdiorr.buildpilot.dto.ProjectResponseDto;

import java.util.List;

public interface ProjectService {

    ProjectResponseDto createProject(ProjectRequestDto request);

    List<ProjectResponseDto> findAll();

    ProjectResponseDto findById(Long id);

    ProjectResponseDto updateProject(Long id, ProjectRequestDto request);

    void deleteProject(Long id);
}
