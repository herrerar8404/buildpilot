package com.acdiorr.buildpilot.repository;

import com.acdiorr.buildpilot.entity.ConstructionTemplate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConstructionTemplateRepository extends JpaRepository<ConstructionTemplate, Long> {

    boolean existsByNameIgnoreCase(String name);

    @EntityGraph(attributePaths = "templateMaterials")
    List<ConstructionTemplate> findAll();

    @EntityGraph(attributePaths = "templateMaterials")
    Optional<ConstructionTemplate> findById(Long id);
}

