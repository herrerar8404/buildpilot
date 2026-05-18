package com.acdiorr.buildpilot.exception;

public class ElementMaterialNotFoundException extends RuntimeException {

    public ElementMaterialNotFoundException(Long id) {
        super("Element material not found with id: " + id);
    }

    public ElementMaterialNotFoundException(String message) {
        super(message);
    }
}

