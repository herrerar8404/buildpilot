package com.acdiorr.buildpilot.controller;

import com.acdiorr.buildpilot.dto.QuotationRequestDto;
import com.acdiorr.buildpilot.dto.QuotationResponseDto;
import com.acdiorr.buildpilot.service.QuotationPdfService;
import com.acdiorr.buildpilot.service.QuotationService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Quotations", description = "Financial engine for project quotations")
public class QuotationController {

    private final QuotationService quotationService;
    private final QuotationPdfService quotationPdfService;

    @Operation(summary = "Create the main quotation for a project")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Quotation created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "409", description = "Project already has a quotation")
    })
    @PostMapping("/api/v1/projects/{projectId}/quotation")
    public ResponseEntity<QuotationResponseDto> createQuotation(
            @PathVariable Long projectId,
            @Valid @RequestBody QuotationRequestDto request) {

        log.info("POST /api/v1/projects/{}/quotation", projectId);
        QuotationResponseDto created = quotationService.createQuotation(projectId, request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/quotations/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Get quotation by project ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quotation found"),
            @ApiResponse(responseCode = "404", description = "Project or quotation not found")
    })
    @GetMapping("/api/v1/projects/{projectId}/quotation")
    public ResponseEntity<QuotationResponseDto> getByProject(@PathVariable Long projectId) {
        log.info("GET /api/v1/projects/{}/quotation", projectId);
        return ResponseEntity.ok(quotationService.getByProject(projectId));
    }

    @Operation(summary = "Get quotation by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quotation found"),
            @ApiResponse(responseCode = "404", description = "Quotation not found")
    })
    @GetMapping("/api/v1/quotations/{id}")
    public ResponseEntity<QuotationResponseDto> getById(@PathVariable Long id) {
        log.info("GET /api/v1/quotations/{}", id);
        return ResponseEntity.ok(quotationService.getById(id));
    }

    @Operation(summary = "Update quotation input costs and recalculate totals")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quotation updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Quotation not found")
    })
    @PutMapping("/api/v1/quotations/{id}")
    public ResponseEntity<QuotationResponseDto> updateQuotation(
            @PathVariable Long id,
            @Valid @RequestBody QuotationRequestDto request) {

        log.info("PUT /api/v1/quotations/{}", id);
        return ResponseEntity.ok(quotationService.updateQuotation(id, request));
    }

    @Operation(summary = "Delete quotation")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Quotation deleted"),
            @ApiResponse(responseCode = "404", description = "Quotation not found")
    })
    @DeleteMapping("/api/v1/quotations/{id}")
    public ResponseEntity<Void> deleteQuotation(@PathVariable Long id) {
        log.info("DELETE /api/v1/quotations/{}", id);
        quotationService.deleteQuotation(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Download quotation PDF by project ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Quotation PDF generated",
                    content = @Content(mediaType = "application/pdf",
                            schema = @Schema(type = "string", format = "binary"))
            ),
            @ApiResponse(responseCode = "404", description = "Project or quotation not found")
    })
    @GetMapping(value = "/api/v1/projects/{projectId}/quotation/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> downloadQuotationPdf(@PathVariable Long projectId) {
        log.info("GET /api/v1/projects/{}/quotation/pdf", projectId);

        byte[] pdfBytes = quotationPdfService.generateQuotationPdf(projectId);
        ByteArrayResource resource = new ByteArrayResource(pdfBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=quotation-project-" + projectId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(resource);
    }
}

