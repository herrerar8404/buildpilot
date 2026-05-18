package com.acdiorr.buildpilot.mapper;

import com.acdiorr.buildpilot.dto.RoomRequestDto;
import com.acdiorr.buildpilot.dto.RoomResponseDto;
import com.acdiorr.buildpilot.entity.enums.RoomShapeType;
import com.acdiorr.buildpilot.entity.Project;
import com.acdiorr.buildpilot.entity.Room;
import org.springframework.stereotype.Component;

/**
 * Maps between Room entity and its DTOs.
 */
@Component
public class RoomMapper {

    public Room toEntity(RoomRequestDto dto, Project project) {
        RoomShapeType resolvedShapeType = resolveShapeType(dto.getShapeType(), RoomShapeType.RECTANGLE);
        Integer resolvedWallCount = resolveWallCount(dto.getWallCount(), resolvedShapeType, 4);

        return Room.builder()
                .name(dto.getName())
                .roomType(dto.getRoomType())
                .shapeType(resolvedShapeType)
                .wallCount(resolvedWallCount)
                .width(dto.getWidth())
                .length(dto.getLength())
                .height(dto.getHeight())
                .positionX(dto.getPositionX())
                .positionY(dto.getPositionY())
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
                .shapeType(room.getShapeType())
                .wallCount(room.getWallCount())
                .positionX(room.getPositionX())
                .positionY(room.getPositionY())
                .doorCount(room.getDoorCount())
                .windowCount(room.getWindowCount())
                .observations(room.getObservations())
                .projectId(room.getProject().getId())
                .projectName(room.getProject().getProjectName())
                .build();
    }

    public void updateEntityFromDto(RoomRequestDto dto, Room existing) {
        RoomShapeType resolvedShapeType = resolveShapeType(dto.getShapeType(), existing.getShapeType());
        Integer resolvedWallCount = resolveWallCount(dto.getWallCount(), resolvedShapeType, existing.getWallCount());

        existing.setName(dto.getName());
        existing.setRoomType(dto.getRoomType());
        existing.setShapeType(resolvedShapeType);
        existing.setWallCount(resolvedWallCount);
        existing.setWidth(dto.getWidth());
        existing.setLength(dto.getLength());
        existing.setHeight(dto.getHeight());
        existing.setPositionX(dto.getPositionX());
        existing.setPositionY(dto.getPositionY());
        existing.setDoorCount(dto.getDoorCount());
        existing.setWindowCount(dto.getWindowCount());
        existing.setObservations(dto.getObservations());
    }

    private RoomShapeType resolveShapeType(RoomShapeType incoming, RoomShapeType fallback) {
        return incoming != null ? incoming : (fallback != null ? fallback : RoomShapeType.RECTANGLE);
    }

    private Integer resolveWallCount(Integer incoming, RoomShapeType shapeType, Integer fallback) {
        if (incoming != null) {
            return incoming;
        }
        if (fallback != null) {
            return fallback;
        }
        return shapeType == RoomShapeType.RECTANGLE ? 4 : 3;
    }
}
