package com.acdiorr.buildpilot.repository;

import com.acdiorr.buildpilot.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByProjectId(Long projectId);

    boolean existsByProjectId(Long projectId);
}

