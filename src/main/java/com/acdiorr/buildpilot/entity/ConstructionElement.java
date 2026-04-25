package com.acdiorr.buildpilot.entity;

import com.acdiorr.buildpilot.entity.enums.ElementType;
import com.acdiorr.buildpilot.entity.enums.UnitMeasure;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "construction_elements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConstructionElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Element name is required")
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @NotNull(message = "Element type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "element_type", nullable = false, length = 30)
    private ElementType elementType;

    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than zero")
    @Digits(integer = 10, fraction = 4, message = "Quantity must have at most 10 integer digits and 4 decimal places")
    @Column(name = "quantity", precision = 14, scale = 4)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_measure", length = 20)
    private UnitMeasure unitMeasure;

    @DecimalMin(value = "0.0", inclusive = false, message = "Calculated area must be greater than zero")
    @Digits(integer = 10, fraction = 4, message = "Calculated area must have at most 10 integer digits and 4 decimal places")
    @Column(name = "calculated_area", precision = 14, scale = 4)
    private BigDecimal calculatedArea;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ─── Relationship ────────────────────────────────────────────────────────

    @NotNull(message = "Room is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonBackReference("room-elements")
    private Room room;

    @OneToMany(mappedBy = "constructionElement", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("element-materials")
    @Builder.Default
    private List<ElementMaterial> elementMaterials = new ArrayList<>();

    // Convenience helpers to keep both sides in sync
    public void addElementMaterial(ElementMaterial em) {
        elementMaterials.add(em);
        em.setConstructionElement(this);
    }

    public void removeElementMaterial(ElementMaterial em) {
        elementMaterials.remove(em);
        em.setConstructionElement(null);
    }

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

