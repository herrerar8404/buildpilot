package com.acdiorr.buildpilot.repository;

import com.acdiorr.buildpilot.entity.Material;
import com.acdiorr.buildpilot.entity.enums.MaterialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    Optional<Material> findByNameIgnoreCase(String name);

    List<Material> findByMaterialType(MaterialType materialType);

    List<Material> findByIsActiveTrue();

    boolean existsByNameIgnoreCase(String name);
}

