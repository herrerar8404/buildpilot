package com.acdiorr.buildpilot.service.impl;

import com.acdiorr.buildpilot.dto.RoomRequestDto;
import com.acdiorr.buildpilot.dto.RoomResponseDto;
import com.acdiorr.buildpilot.entity.Project;
import com.acdiorr.buildpilot.entity.Room;
import com.acdiorr.buildpilot.exception.ProjectNotFoundException;
import com.acdiorr.buildpilot.exception.RoomNotFoundException;
import com.acdiorr.buildpilot.mapper.RoomMapper;
import com.acdiorr.buildpilot.repository.ProjectRepository;
import com.acdiorr.buildpilot.repository.RoomRepository;
import com.acdiorr.buildpilot.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final ProjectRepository projectRepository;
    private final RoomMapper roomMapper;

    @Override
    @Transactional
    public RoomResponseDto createRoom(Long projectId, RoomRequestDto request) {
        log.info("Creating room in project id: {}", projectId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        Room room = roomMapper.toEntity(request, project);
        return roomMapper.toResponseDto(roomRepository.save(room));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDto> findAllByProject(Long projectId) {
        log.info("Fetching rooms for project id: {}", projectId);
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException(projectId);
        }
        return roomRepository.findByProjectId(projectId)
                .stream()
                .map(roomMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponseDto findById(Long id) {
        log.info("Fetching room id: {}", id);
        return roomRepository.findById(id)
                .map(roomMapper::toResponseDto)
                .orElseThrow(() -> new RoomNotFoundException(id));
    }

    @Override
    @Transactional
    public RoomResponseDto updateRoom(Long id, RoomRequestDto request) {
        log.info("Updating room id: {}", id);
        Room existing = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));
        roomMapper.updateEntityFromDto(request, existing);
        return roomMapper.toResponseDto(roomRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        log.info("Deleting room id: {}", id);
        if (!roomRepository.existsById(id)) {
            throw new RoomNotFoundException(id);
        }
        roomRepository.deleteById(id);
    }
}

