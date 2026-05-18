package com.acdiorr.buildpilot.repository;

import com.acdiorr.buildpilot.entity.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    Optional<Quotation> findByProjectId(Long projectId);

    boolean existsByProjectId(Long projectId);
}

