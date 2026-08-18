package com.acdiorr.buildpilot.exception;

public class TemplateMaterialNotFoundException extends RuntimeException {

    public TemplateMaterialNotFoundException(Long id) {
        super("Template material not found with id: " + id);
    }
}

