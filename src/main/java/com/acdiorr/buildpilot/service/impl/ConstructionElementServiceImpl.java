package com.acdiorr.buildpilot.service.impl;

import com.acdiorr.buildpilot.dto.ConstructionElementRequestDto;
import com.acdiorr.buildpilot.dto.ConstructionElementResponseDto;
import com.acdiorr.buildpilot.entity.ConstructionElement;
import com.acdiorr.buildpilot.entity.Room;
import com.acdiorr.buildpilot.exception.ConstructionElementNotFoundException;
import com.acdiorr.buildpilot.exception.RoomNotFoundException;
import com.acdiorr.buildpilot.mapper.ConstructionElementMapper;
import com.acdiorr.buildpilot.repository.ConstructionElementRepository;
import com.acdiorr.buildpilot.repository.RoomRepository;
import com.acdiorr.buildpilot.service.ConstructionElementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConstructionElementServiceImpl implements ConstructionElementService {

    private final ConstructionElementRepository constructionElementRepository;
    private final RoomRepository roomRepository;
    private final ConstructionElementMapper constructionElementMapper;

    @Override
    @Transactional
    public ConstructionElementResponseDto createConstructionElement(Long roomId, ConstructionElementRequestDto request) {
        log.info("Creating construction element in room id: {}", roomId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));

        ConstructionElement element = constructionElementMapper.toEntity(request, room);
        return constructionElementMapper.toResponseDto(constructionElementRepository.save(element));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConstructionElementResponseDto> getByRoom(Long roomId) {
        log.info("Fetching construction elements for room id: {}", roomId);
        if (!roomRepository.existsById(roomId)) {
            throw new RoomNotFoundException(roomId);
        }

        return constructionElementRepository.findByRoomId(roomId)
                .stream()
                .map(constructionElementMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ConstructionElementResponseDto getById(Long id) {
        log.info("Fetching construction element id: {}", id);
        return constructionElementRepository.findById(id)
                .map(constructionElementMapper::toResponseDto)
                .orElseThrow(() -> new ConstructionElementNotFoundException(id));
    }

    @Override
    @Transactional
    public ConstructionElementResponseDto updateConstructionElement(Long id, ConstructionElementRequestDto request) {
        log.info("Updating construction element id: {}", id);
        ConstructionElement existing = constructionElementRepository.findById(id)
                .orElseThrow(() -> new ConstructionElementNotFoundException(id));

        constructionElementMapper.updateEntityFromDto(request, existing);
        return constructionElementMapper.toResponseDto(constructionElementRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteConstructionElement(Long id) {
        log.info("Deleting construction element id: {}", id);
        if (!constructionElementRepository.existsById(id)) {
            throw new ConstructionElementNotFoundException(id);
        }
        constructionElementRepository.deleteById(id);
    }
}

