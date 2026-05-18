package com.acdiorr.buildpilot.exception;

public class DuplicateElementMaterialException extends RuntimeException {

    public DuplicateElementMaterialException(Long constructionElementId, Long materialId) {
        super("Material id " + materialId +
              " is already assigned to construction element id " + constructionElementId);
    }
}

