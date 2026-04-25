package com.acdiorr.buildpilot.exception;

public class MaterialDuplicateNameException extends RuntimeException {

    public MaterialDuplicateNameException(String name) {
        super("A material with the name '" + name + "' already exists");
    }
}

