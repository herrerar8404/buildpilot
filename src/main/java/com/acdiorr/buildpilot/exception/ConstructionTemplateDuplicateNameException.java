package com.acdiorr.buildpilot.exception;

public class ConstructionTemplateDuplicateNameException extends RuntimeException {

    public ConstructionTemplateDuplicateNameException(String name) {
        super("A construction template with name '" + name + "' already exists");
    }
}

