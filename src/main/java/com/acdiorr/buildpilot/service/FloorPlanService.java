package com.acdiorr.buildpilot.service;

import com.acdiorr.buildpilot.dto.FloorPlanResponseDto;

public interface FloorPlanService {

    FloorPlanResponseDto getFloorPlan(Long projectId);
}

