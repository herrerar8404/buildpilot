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
@Table(
        name = "template_materials",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_template_materials_template_material",
                columnNames = {"construction_template_id", "material_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Quantity per unit is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity per unit must be greater than zero")
    @Digits(integer = 10, fraction = 4, message = "Quantity per unit must have at most 10 integer digits and 4 decimal places")
    @Column(name = "quantity_per_unit", nullable = false, precision = 14, scale = 4)
    private BigDecimal quantityPerUnit;

    @DecimalMin(value = "0.0", inclusive = true, message = "Waste percentage must be zero or greater")
    @Digits(integer = 5, fraction = 2, message = "Waste percentage must have at most 5 integer digits and 2 decimal places")
    @Column(name = "waste_percentage", precision = 7, scale = 2)
    private BigDecimal wastePercentage;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @NotNull(message = "Construction template is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_template_id", nullable = false)
    @JsonBackReference("template-materials")
    private ConstructionTemplate constructionTemplate;

    @NotNull(message = "Material is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.wastePercentage == null) {
            this.wastePercentage = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.wastePercentage == null) {
            this.wastePercentage = BigDecimal.ZERO;
        }
    }
}

