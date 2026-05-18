package com.acdiorr.buildpilot.repository;

import com.acdiorr.buildpilot.entity.ConstructionElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConstructionElementRepository extends JpaRepository<ConstructionElement, Long> {

    List<ConstructionElement> findByRoomId(Long roomId);
}

