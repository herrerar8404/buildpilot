package com.acdiorr.buildpilot.repository;

import com.acdiorr.buildpilot.entity.Project;
import com.acdiorr.buildpilot.entity.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByClientNameContainingIgnoreCase(String clientName);

    boolean existsByProjectName(String projectName);
}

