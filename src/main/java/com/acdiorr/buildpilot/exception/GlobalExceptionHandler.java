package com.acdiorr.buildpilot.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Centralized exception handling for the entire REST API.
 * All error responses follow the same ApiError structure.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 – project not found
    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ApiError> handleProjectNotFound(
            ProjectNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Project not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    // 404 – room not found
    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<ApiError> handleRoomNotFound(
            RoomNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Room not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    // 404 – material not found
    @ExceptionHandler(MaterialNotFoundException.class)
    public ResponseEntity<ApiError> handleMaterialNotFound(
            MaterialNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Material not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    // 404 – construction element not found
    @ExceptionHandler(ConstructionElementNotFoundException.class)
    public ResponseEntity<ApiError> handleConstructionElementNotFound(
            ConstructionElementNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Construction element not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    // 404 – element material not found
    @ExceptionHandler(ElementMaterialNotFoundException.class)
    public ResponseEntity<ApiError> handleElementMaterialNotFound(
            ElementMaterialNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Element material not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    // 404 – quotation not found
    @ExceptionHandler(QuotationNotFoundException.class)
    public ResponseEntity<ApiError> handleQuotationNotFound(
            QuotationNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Quotation not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    // 404 - construction template not found
    @ExceptionHandler(ConstructionTemplateNotFoundException.class)
    public ResponseEntity<ApiError> handleConstructionTemplateNotFound(
            ConstructionTemplateNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Construction template not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    // 404 - template material not found
    @ExceptionHandler(TemplateMaterialNotFoundException.class)
    public ResponseEntity<ApiError> handleTemplateMaterialNotFound(
            TemplateMaterialNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Template material not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    // 409 – material already assigned to construction element
    @ExceptionHandler(DuplicateElementMaterialException.class)
    public ResponseEntity<ApiError> handleDuplicateElementMaterial(
            DuplicateElementMaterialException ex,
            HttpServletRequest request) {

        log.warn("Duplicate element material: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    // 409 – duplicate material name
    @ExceptionHandler(MaterialDuplicateNameException.class)
    public ResponseEntity<ApiError> handleMaterialDuplicateName(
            MaterialDuplicateNameException ex,
            HttpServletRequest request) {

        log.warn("Duplicate material name: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    // 409 – project already has a quotation
    @ExceptionHandler(DuplicateQuotationException.class)
    public ResponseEntity<ApiError> handleDuplicateQuotation(
            DuplicateQuotationException ex,
            HttpServletRequest request) {

        log.warn("Duplicate quotation: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    // 409 - duplicate construction template name
    @ExceptionHandler(ConstructionTemplateDuplicateNameException.class)
    public ResponseEntity<ApiError> handleConstructionTemplateDuplicateName(
            ConstructionTemplateDuplicateNameException ex,
            HttpServletRequest request) {

        log.warn("Duplicate construction template name: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    // 409 - material already assigned to construction template
    @ExceptionHandler(DuplicateTemplateMaterialException.class)
    public ResponseEntity<ApiError> handleDuplicateTemplateMaterial(
            DuplicateTemplateMaterialException ex,
            HttpServletRequest request) {

        log.warn("Duplicate template material: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    // 409 - construction element already has assigned materials
    @ExceptionHandler(ConstructionElementAlreadyMaterializedException.class)
    public ResponseEntity<ApiError> handleConstructionElementAlreadyMaterialized(
            ConstructionElementAlreadyMaterializedException ex,
            HttpServletRequest request) {

        log.warn("Blocked template apply due to existing materials: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    // 400 - invalid template application input/state
    @ExceptionHandler(InvalidTemplateApplicationException.class)
    public ResponseEntity<ApiError> handleInvalidTemplateApplication(
            InvalidTemplateApplicationException ex,
            HttpServletRequest request) {

        log.warn("Invalid template application: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    // 400 – @Valid constraint violations
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .sorted()
                .toList();

        log.warn("Validation failed: {}", fieldErrors);
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", request, fieldErrors);
    }

    // 500 – any unexpected exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request, null);
    }

    // ─── helpers ────────────────────────────────────────────────────────────

    private ResponseEntity<ApiError> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            List<String> fieldErrors) {

        ApiError error = ApiError.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(status).body(error);
    }
}
