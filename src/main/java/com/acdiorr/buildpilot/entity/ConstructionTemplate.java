package com.acdiorr.buildpilot.entity;

import com.acdiorr.buildpilot.entity.enums.ConstructionTemplateCategory;
import com.acdiorr.buildpilot.entity.enums.UnitMeasure;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "construction_templates",
        uniqueConstraints = @UniqueConstraint(name = "uq_construction_templates_name", columnNames = "name")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConstructionTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Template name is required")
    @Column(name = "name", nullable = false, unique = true, length = 150)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Template category is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private ConstructionTemplateCategory category;

    @NotNull(message = "Template unit type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "unit_type", nullable = false, length = 20)
    private UnitMeasure unitType;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "constructionTemplate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("template-materials")
    @Builder.Default
    private List<TemplateMaterial> templateMaterials = new ArrayList<>();

    public void addTemplateMaterial(TemplateMaterial templateMaterial) {
        templateMaterials.add(templateMaterial);
        templateMaterial.setConstructionTemplate(this);
    }

    public void removeTemplateMaterial(TemplateMaterial templateMaterial) {
        templateMaterials.remove(templateMaterial);
        templateMaterial.setConstructionTemplate(null);
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.active == null) {
            this.active = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

