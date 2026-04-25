package com.acdiorr.buildpilot.service.impl;

import com.acdiorr.buildpilot.dto.MaterialRequestDto;
import com.acdiorr.buildpilot.dto.MaterialResponseDto;
import com.acdiorr.buildpilot.entity.Material;
import com.acdiorr.buildpilot.exception.MaterialDuplicateNameException;
import com.acdiorr.buildpilot.exception.MaterialNotFoundException;
import com.acdiorr.buildpilot.mapper.MaterialMapper;
import com.acdiorr.buildpilot.repository.MaterialRepository;
import com.acdiorr.buildpilot.service.MaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;

    @Override
    @Transactional
    public MaterialResponseDto createMaterial(MaterialRequestDto request) {
        log.info("Creating material: {}", request.getName());
        if (materialRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new MaterialDuplicateNameException(request.getName());
        }
        Material material = materialMapper.toEntity(request);
        return materialMapper.toResponseDto(materialRepository.save(material));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaterialResponseDto> getAllMaterials() {
        log.info("Fetching all materials");
        return materialRepository.findAll()
                .stream()
                .map(materialMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MaterialResponseDto getMaterialById(Long id) {
        log.info("Fetching material id: {}", id);
        return materialRepository.findById(id)
                .map(materialMapper::toResponseDto)
                .orElseThrow(() -> new MaterialNotFoundException(id));
    }

    @Override
    @Transactional
    public MaterialResponseDto updateMaterial(Long id, MaterialRequestDto request) {
        log.info("Updating material id: {}", id);
        Material existing = materialRepository.findById(id)
                .orElseThrow(() -> new MaterialNotFoundException(id));

        // Check name uniqueness only if the name is actually changing
        String newName = request.getName().trim();
        if (!existing.getName().equalsIgnoreCase(newName)
                && materialRepository.existsByNameIgnoreCase(newName)) {
            throw new MaterialDuplicateNameException(newName);
        }

        materialMapper.updateEntityFromDto(request, existing);
        return materialMapper.toResponseDto(materialRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteMaterial(Long id) {
        log.info("Deleting material id: {}", id);
        if (!materialRepository.existsById(id)) {
            throw new MaterialNotFoundException(id);
        }
        materialRepository.deleteById(id);
    }
}

