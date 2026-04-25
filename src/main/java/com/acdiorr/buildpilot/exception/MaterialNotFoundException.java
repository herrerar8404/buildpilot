package com.acdiorr.buildpilot.exception;

public class MaterialNotFoundException extends RuntimeException {

    public MaterialNotFoundException(Long id) {
        super("Material not found with id: " + id);
    }

    public MaterialNotFoundException(String message) {
        super(message);
    }
}

