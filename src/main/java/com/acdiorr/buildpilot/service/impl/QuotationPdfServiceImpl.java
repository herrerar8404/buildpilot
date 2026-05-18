package com.acdiorr.buildpilot.service.impl;

import com.acdiorr.buildpilot.entity.Project;
import com.acdiorr.buildpilot.entity.Quotation;
import com.acdiorr.buildpilot.exception.ProjectNotFoundException;
import com.acdiorr.buildpilot.exception.QuotationNotFoundException;
import com.acdiorr.buildpilot.repository.ProjectRepository;
import com.acdiorr.buildpilot.repository.QuotationRepository;
import com.acdiorr.buildpilot.service.QuotationPdfService;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuotationPdfServiceImpl implements QuotationPdfService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ProjectRepository projectRepository;
    private final QuotationRepository quotationRepository;

    @Override
    @Transactional(readOnly = true)
    public byte[] generateQuotationPdf(Long projectId) {
        log.info("Generating quotation PDF for project id: {}", projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        Quotation quotation = quotationRepository.findByProjectId(projectId)
                .orElseThrow(() -> new QuotationNotFoundException("Quotation not found for project id: " + projectId));

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.setMargins(36, 36, 36, 36);
            addHeader(document);
            addProjectSection(document, project);
            addFinancialSection(document, quotation);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate quotation PDF", ex);
        }
    }

    private void addHeader(Document document) {
        Paragraph title = new Paragraph("BuildPilot Construction Quotation")
                .setBold()
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(33, 37, 41));

        Paragraph generatedAt = new Paragraph("Generated at: " + DATE_TIME_FORMATTER.format(LocalDateTime.now()))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(108, 117, 125))
                .setMarginBottom(20);

        document.add(title);
        document.add(generatedAt);
    }

    private void addProjectSection(Document document, Project project) {
        Paragraph sectionTitle = new Paragraph("Project Information")
                .setBold()
                .setFontSize(13)
                .setMarginBottom(8)
                .setFontColor(new DeviceRgb(33, 37, 41));

        Table table = new Table(new float[]{180f, 330f});
        table.setWidth(UnitValue.createPercentValue(100));

        addInfoRow(table, "Project Name", project.getProjectName());
        addInfoRow(table, "Client", project.getClientName());
        addInfoRow(table, "Construction Type", project.getConstructionType());
        addInfoRow(table, "Address", project.getAddress());
        addInfoRow(table, "Description", project.getDescription());
        addInfoRow(table, "Creation Date", project.getCreationDate() != null
                ? DATE_FORMATTER.format(project.getCreationDate())
                : "-");

        table.setMarginBottom(18);
        document.add(sectionTitle);
        document.add(table);
    }

    private void addFinancialSection(Document document, Quotation quotation) {
        Paragraph sectionTitle = new Paragraph("Financial Summary")
                .setBold()
                .setFontSize(13)
                .setMarginBottom(8)
                .setFontColor(new DeviceRgb(33, 37, 41));

        Table table = new Table(new float[]{220f, 140f});
        table.setWidth(UnitValue.createPercentValue(70));

        addMoneyRow(table, "Material Cost", quotation.getMaterialCost());
        addMoneyRow(table, "Labor Cost", quotation.getLaborCost());
        addMoneyRow(table, "Indirect Cost", quotation.getIndirectCost());
        addPercentRow(table, "Profit Margin", quotation.getProfitMargin());
        addMoneyRow(table, "Subtotal", quotation.getSubtotal());

        Cell labelCell = createCell("Total Cost", true);
        Cell valueCell = createCell(formatMoney(quotation.getTotalCost()), true);
        table.addCell(labelCell);
        table.addCell(valueCell);

        table.setMarginBottom(8);
        document.add(sectionTitle);
        document.add(table);

        if (quotation.getNotes() != null && !quotation.getNotes().isBlank()) {
            Paragraph notes = new Paragraph("Notes: " + quotation.getNotes())
                    .setFontSize(10)
                    .setFontColor(new DeviceRgb(73, 80, 87));
            document.add(notes);
        }
    }

    private void addInfoRow(Table table, String label, String value) {
        table.addCell(createCell(label, false));
        table.addCell(createCell(value != null && !value.isBlank() ? value : "-", false));
    }

    private void addMoneyRow(Table table, String label, BigDecimal value) {
        table.addCell(createCell(label, false));
        table.addCell(createCell(formatMoney(value), false));
    }

    private void addPercentRow(Table table, String label, BigDecimal value) {
        table.addCell(createCell(label, false));
        BigDecimal normalized = value == null ? BigDecimal.ZERO : value.setScale(2, RoundingMode.HALF_UP);
        table.addCell(createCell(normalized.toPlainString() + " %", false));
    }

    private Cell createCell(String text, boolean highlight) {
        Cell cell = new Cell().add(new Paragraph(text).setFontSize(10));
        cell.setBorder(new SolidBorder(new DeviceRgb(222, 226, 230), 1));
        cell.setPadding(6);
        if (highlight) {
            cell.setBackgroundColor(new DeviceRgb(248, 249, 250));
        }
        return cell;
    }

    private String formatMoney(BigDecimal value) {
        BigDecimal normalized = value == null ? BigDecimal.ZERO : value.setScale(2, RoundingMode.HALF_UP);
        return "$ " + normalized.toPlainString();
    }
}


