package com.acdiorr.buildpilot.repository;

import com.acdiorr.buildpilot.entity.ElementMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElementMaterialRepository extends JpaRepository<ElementMaterial, Long> {

    /**
     * Fetch all ElementMaterial records for a given ConstructionElement,
     * eagerly loading the associated Material to avoid N+1 queries.
     */
    @Query("SELECT em FROM ElementMaterial em " +
           "JOIN FETCH em.material " +
           "WHERE em.constructionElement.id = :constructionElementId")
    List<ElementMaterial> findByConstructionElementId(@Param("constructionElementId") Long constructionElementId);

    boolean existsByConstructionElementIdAndMaterialId(Long constructionElementId, Long materialId);

    @Query("SELECT SUM(em.calculatedCost) FROM ElementMaterial em " +
           "JOIN em.constructionElement ce " +
           "JOIN ce.room r " +
           "JOIN r.project p " +
           "WHERE p.id = :projectId")
    java.math.BigDecimal sumCalculatedCostByProjectId(@Param("projectId") Long projectId);
}
