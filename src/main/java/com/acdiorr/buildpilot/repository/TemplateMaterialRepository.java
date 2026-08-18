package com.acdiorr.buildpilot.repository;

import com.acdiorr.buildpilot.entity.TemplateMaterial;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateMaterialRepository extends JpaRepository<TemplateMaterial, Long> {

    @Query("SELECT tm FROM TemplateMaterial tm JOIN FETCH tm.material JOIN FETCH tm.constructionTemplate WHERE tm.constructionTemplate.id = :constructionTemplateId")
    List<TemplateMaterial> findByConstructionTemplateId(@Param("constructionTemplateId") Long constructionTemplateId);

    @Query("SELECT tm FROM TemplateMaterial tm JOIN FETCH tm.material JOIN FETCH tm.constructionTemplate WHERE tm.id = :id")
    java.util.Optional<TemplateMaterial> findDetailedById(@Param("id") Long id);

    boolean existsByConstructionTemplateIdAndMaterialId(Long constructionTemplateId, Long materialId);
}

