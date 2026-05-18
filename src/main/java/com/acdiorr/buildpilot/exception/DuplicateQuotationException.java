package com.acdiorr.buildpilot.exception;

public class DuplicateQuotationException extends RuntimeException {

    public DuplicateQuotationException(Long projectId) {
        super("Project id " + projectId + " already has a quotation");
    }
}

