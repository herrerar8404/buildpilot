package com.acdiorr.buildpilot.exception;

public class ConstructionElementNotFoundException extends RuntimeException {

    public ConstructionElementNotFoundException(Long id) {
        super("Construction element not found with id: " + id);
    }

    public ConstructionElementNotFoundException(String message) {
        super(message);
    }
}

