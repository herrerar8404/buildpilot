package com.acdiorr.buildpilot.exception;

public class ConstructionTemplateNotFoundException extends RuntimeException {

    public ConstructionTemplateNotFoundException(Long id) {
        super("Construction template not found with id: " + id);
    }
}

