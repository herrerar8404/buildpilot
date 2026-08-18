package com.acdiorr.buildpilot.exception;

public class DuplicateTemplateMaterialException extends RuntimeException {

    public DuplicateTemplateMaterialException(Long constructionTemplateId, Long materialId) {
        super("Material id " + materialId +
                " is already assigned to construction template id " + constructionTemplateId);
    }
}

