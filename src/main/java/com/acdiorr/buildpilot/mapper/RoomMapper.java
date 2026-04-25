package com.acdiorr.buildpilot.mapper;

import com.acdiorr.buildpilot.dto.RoomRequestDto;
import com.acdiorr.buildpilot.dto.RoomResponseDto;
import com.acdiorr.buildpilot.entity.Project;
import com.acdiorr.buildpilot.entity.Room;
import org.springframework.stereotype.Component;

/**
 * Maps between Room entity and its DTOs.
 */
@Component
public class RoomMapper {

    public Room toEntity(RoomRequestDto dto, Project project) {
        return Room.builder()
                .name(dto.getName())
                .roomType(dto.getRoomType())
                .width(dto.getWidth())
                .length(dto.getLength())
                .height(dto.getHeight())
                .doorCount(dto.getDoorCount())
                .windowCount(dto.getWindowCount())
                .observations(dto.getObservations())
                .project(project)
                .build();
    }

    public RoomResponseDto toResponseDto(Room room) {
        return RoomResponseDto.builder()
                .id(room.getId())
                .name(room.getName())
                .roomType(room.getRoomType())
                .width(room.getWidth())
                .length(room.getLength())
                .height(room.getHeight())
                .doorCount(room.getDoorCount())
                .windowCount(room.getWindowCount())
                .observations(room.getObservations())
                .projectId(room.getProject().getId())
                .projectName(room.getProject().getProjectName())
                .build();
    }

    public void updateEntityFromDto(RoomRequestDto dto, Room existing) {
        existing.setName(dto.getName());
        existing.setRoomType(dto.getRoomType());
        existing.setWidth(dto.getWidth());
        existing.setLength(dto.getLength());
        existing.setHeight(dto.getHeight());
        existing.setDoorCount(dto.getDoorCount());
        existing.setWindowCount(dto.getWindowCount());
        existing.setObservations(dto.getObservations());
    }
}

