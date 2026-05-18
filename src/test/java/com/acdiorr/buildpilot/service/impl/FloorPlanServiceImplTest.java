package com.acdiorr.buildpilot.service.impl;

import com.acdiorr.buildpilot.dto.FloorPlanResponseDto;
import com.acdiorr.buildpilot.entity.Project;
import com.acdiorr.buildpilot.entity.Room;
import com.acdiorr.buildpilot.entity.enums.RoomType;
import com.acdiorr.buildpilot.exception.ProjectNotFoundException;
import com.acdiorr.buildpilot.repository.ProjectRepository;
import com.acdiorr.buildpilot.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FloorPlanServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private FloorPlanServiceImpl floorPlanService;

    @Test
    void getFloorPlan_shouldReturnProjectGeometryAndRooms() {
        Long projectId = 1L;

        Project project = Project.builder()
                .id(projectId)
                .projectName("Campus North")
                .landWidth(new BigDecimal("20.00"))
                .landLength(new BigDecimal("30.00"))
                .build();

        Room roomA = Room.builder()
                .id(2L)
                .name("Living Room")
                .roomType(RoomType.LIVING_ROOM)
                .width(new BigDecimal("5.00"))
                .length(new BigDecimal("4.00"))
                .positionX(new BigDecimal("2.00"))
                .positionY(new BigDecimal("3.00"))
                .build();

        Room roomB = Room.builder()
                .id(1L)
                .name("Kitchen")
                .roomType(RoomType.KITCHEN)
                .width(new BigDecimal("3.50"))
                .length(new BigDecimal("3.00"))
                .positionX(new BigDecimal("0.50"))
                .positionY(new BigDecimal("1.00"))
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(roomRepository.findByProjectId(projectId)).thenReturn(List.of(roomA, roomB));

        FloorPlanResponseDto result = floorPlanService.getFloorPlan(projectId);

        assertEquals(projectId, result.getProjectId());
        assertEquals("Campus North", result.getProjectName());
        assertEquals(new BigDecimal("20.00"), result.getLandWidth());
        assertEquals(new BigDecimal("30.00"), result.getLandLength());
        assertEquals(2, result.getRooms().size());

        // Sorted by room id to keep deterministic rendering order.
        assertEquals(1L, result.getRooms().get(0).getId());
        assertEquals("Kitchen", result.getRooms().get(0).getName());
        assertEquals(new BigDecimal("0.50"), result.getRooms().get(0).getPositionX());
    }

    @Test
    void getFloorPlan_shouldThrowWhenProjectDoesNotExist() {
        Long projectId = 99L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> floorPlanService.getFloorPlan(projectId));
    }
}

