package com.acdiorr.buildpilot.exception;

public class QuotationNotFoundException extends RuntimeException {

    public QuotationNotFoundException(Long id) {
        super("Quotation not found with id: " + id);
    }

    public QuotationNotFoundException(String message) {
        super(message);
    }
}

