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
@Table(name = "element_materials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElementMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ─── Calculation fields ──────────────────────────────────────────────────

    @NotNull(message = "Required quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Required quantity must be greater than zero")
    @Digits(integer = 10, fraction = 4, message = "Max 10 integer digits and 4 decimal places")
    @Column(name = "required_quantity", nullable = false, precision = 14, scale = 4)
    private BigDecimal requiredQuantity;

    @DecimalMin(value = "0.0", inclusive = false, message = "Performance per m² must be greater than zero")
    @Digits(integer = 10, fraction = 4, message = "Max 10 integer digits and 4 decimal places")
    @Column(name = "performance_per_m2", precision = 14, scale = 4)
    private BigDecimal performancePerM2;

    // Stored as a percentage value, e.g. 5.00 = 5%
    @DecimalMin(value = "0.0", inclusive = true, message = "Waste percentage must be zero or greater")
    @Digits(integer = 5, fraction = 2, message = "Max 5 integer digits and 2 decimal places")
    @Column(name = "waste_percentage", precision = 7, scale = 2)
    private BigDecimal wastePercentage;

    // Persisted business result: requiredQuantity * (1 + wastePercentage / 100)
    @DecimalMin(value = "0.0", inclusive = false, message = "Final quantity must be greater than zero")
    @Digits(integer = 10, fraction = 4, message = "Max 10 integer digits and 4 decimal places")
    @Column(name = "final_quantity", precision = 14, scale = 4)
    private BigDecimal finalQuantity;

    // Persisted business result: finalQuantity * material.unitPrice
    @DecimalMin(value = "0.0", inclusive = true, message = "Calculated cost must be zero or greater")
    @Digits(integer = 14, fraction = 2, message = "Max 14 integer digits and 2 decimal places")
    @Column(name = "calculated_cost", precision = 16, scale = 2)
    private BigDecimal calculatedCost;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ─── Relationships ───────────────────────────────────────────────────────

    @NotNull(message = "Construction element is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_element_id", nullable = false)
    @JsonBackReference("element-materials")
    private ConstructionElement constructionElement;

    @NotNull(message = "Material is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    // ─── Lifecycle callbacks ─────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

