package com.acdiorr.buildpilot.service.impl;

import com.acdiorr.buildpilot.dto.FloorPlanResponseDto;
import com.acdiorr.buildpilot.dto.FloorPlanRoomDto;
import com.acdiorr.buildpilot.entity.Project;
import com.acdiorr.buildpilot.entity.Room;
import com.acdiorr.buildpilot.exception.ProjectNotFoundException;
import com.acdiorr.buildpilot.repository.ProjectRepository;
import com.acdiorr.buildpilot.repository.RoomRepository;
import com.acdiorr.buildpilot.service.FloorPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FloorPlanServiceImpl implements FloorPlanService {

    private final ProjectRepository projectRepository;
    private final RoomRepository roomRepository;

    @Override
    @Transactional(readOnly = true)
    public FloorPlanResponseDto getFloorPlan(Long projectId) {
        log.info("Fetching floor plan for project id: {}", projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        List<FloorPlanRoomDto> roomDtos = roomRepository.findByProjectId(projectId)
                .stream()
                .sorted(Comparator.comparing(Room::getId, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toFloorPlanRoomDto)
                .toList();

        return FloorPlanResponseDto.builder()
                .projectId(project.getId())
                .projectName(project.getProjectName())
                .landWidth(project.getLandWidth())
                .landLength(project.getLandLength())
                .rooms(roomDtos)
                .build();
    }

    private FloorPlanRoomDto toFloorPlanRoomDto(Room room) {
        return FloorPlanRoomDto.builder()
                .id(room.getId())
                .name(room.getName())
                .roomType(room.getRoomType())
                .shapeType(room.getShapeType())
                .wallCount(room.getWallCount())
                .width(room.getWidth())
                .length(room.getLength())
                .positionX(room.getPositionX())
                .positionY(room.getPositionY())
                .build();
    }
}
