package com.acdiorr.buildpilot.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Financial fields ──────────────────────────────────────────────────────

    @NotNull(message = "Material cost is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Material cost must be zero or positive")
    @Digits(integer = 12, fraction = 2, message = "Material cost must have at most 12 integer digits and 2 decimal places")
    @Column(name = "material_cost", nullable = false, precision = 14, scale = 2)
    private BigDecimal materialCost;

    @NotNull(message = "Labor cost is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Labor cost must be zero or positive")
    @Digits(integer = 12, fraction = 2, message = "Labor cost must have at most 12 integer digits and 2 decimal places")
    @Column(name = "labor_cost", nullable = false, precision = 14, scale = 2)
    private BigDecimal laborCost;

    @NotNull(message = "Indirect cost is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Indirect cost must be zero or positive")
    @Digits(integer = 12, fraction = 2, message = "Indirect cost must have at most 12 integer digits and 2 decimal places")
    @Column(name = "indirect_cost", nullable = false, precision = 14, scale = 2)
    private BigDecimal indirectCost;

    /**
     * Profit margin expressed as a percentage (e.g. 15.00 = 15 %).
     */
    @NotNull(message = "Profit margin is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Profit margin must be zero or positive")
    @Digits(integer = 5, fraction = 2, message = "Profit margin must have at most 5 integer digits and 2 decimal places")
    @Column(name = "profit_margin", nullable = false, precision = 7, scale = 2)
    private BigDecimal profitMargin;

    /**
     * Subtotal = materialCost + laborCost + indirectCost.
     * Persisted as a business result; must be kept in sync by the service layer.
     */
    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Subtotal must be zero or positive")
    @Digits(integer = 12, fraction = 2, message = "Subtotal must have at most 12 integer digits and 2 decimal places")
    @Column(name = "subtotal", nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;

    /**
     * Total cost = subtotal + (subtotal * profitMargin / 100).
     * Persisted as a business result; must be kept in sync by the service layer.
     */
    @NotNull(message = "Total cost is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Total cost must be zero or positive")
    @Digits(integer = 12, fraction = 2, message = "Total cost must have at most 12 integer digits and 2 decimal places")
    @Column(name = "total_cost", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ── Audit fields ──────────────────────────────────────────────────────────

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Relationship ──────────────────────────────────────────────────────────

    /**
     * Owning side of the OneToOne relationship with Project.
     * The FK column {@code project_id} lives in the {@code quotations} table.
     */
    @NotNull(message = "Project is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_quotations_project_id"))
    @JsonBackReference("project-quotation")
    private Project project;

    // ── Lifecycle callbacks ───────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

