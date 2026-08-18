package com.acdiorr.buildpilot.exception;

public class ConstructionElementAlreadyMaterializedException extends RuntimeException {

    public ConstructionElementAlreadyMaterializedException(Long constructionElementId) {
        super("Construction element id " + constructionElementId
                + " already has assigned materials. Template application is blocked to avoid destructive regeneration.");
    }
}

