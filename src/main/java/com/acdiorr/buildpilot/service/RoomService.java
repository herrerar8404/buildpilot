package com.acdiorr.buildpilot.service;

import com.acdiorr.buildpilot.dto.RoomRequestDto;
import com.acdiorr.buildpilot.dto.RoomResponseDto;

import java.util.List;

public interface RoomService {

    RoomResponseDto createRoom(Long projectId, RoomRequestDto request);

    List<RoomResponseDto> findAllByProject(Long projectId);

    RoomResponseDto findById(Long id);

    RoomResponseDto updateRoom(Long id, RoomRequestDto request);

    void deleteRoom(Long id);
}

