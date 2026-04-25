package com.acdiorr.buildpilot.service.impl;

import com.acdiorr.buildpilot.dto.ProjectRequestDto;
import com.acdiorr.buildpilot.dto.ProjectResponseDto;
import com.acdiorr.buildpilot.entity.Project;
import com.acdiorr.buildpilot.exception.ProjectNotFoundException;
import com.acdiorr.buildpilot.mapper.ProjectMapper;
import com.acdiorr.buildpilot.repository.ProjectRepository;
import com.acdiorr.buildpilot.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional
    public ProjectResponseDto createProject(ProjectRequestDto request) {
        Project project = projectMapper.toEntity(request);
        if (project.getCreationDate() == null) {
            project.setCreationDate(LocalDate.now());
        }
        log.info("Creating new project: {}", project.getProjectName());
        return projectMapper.toResponseDto(projectRepository.save(project));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDto> findAll() {
        log.info("Fetching all projects");
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDto findById(Long id) {
        log.info("Fetching project with id: {}", id);
        return projectRepository.findById(id)
                .map(projectMapper::toResponseDto)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    @Override
    @Transactional
    public ProjectResponseDto updateProject(Long id, ProjectRequestDto request) {
        log.info("Updating project with id: {}", id);
        Project existing = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        projectMapper.updateEntityFromDto(request, existing);
        return projectMapper.toResponseDto(projectRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        log.info("Deleting project with id: {}", id);
        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException(id);
        }
        projectRepository.deleteById(id);
    }
}
