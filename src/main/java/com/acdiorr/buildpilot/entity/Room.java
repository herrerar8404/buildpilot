package com.acdiorr.buildpilot.entity;

import com.acdiorr.buildpilot.entity.enums.RoomShapeType;
import com.acdiorr.buildpilot.entity.enums.RoomType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Room name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type")
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    @Column(name = "shape_type", nullable = false)
    @Builder.Default
    private RoomShapeType shapeType = RoomShapeType.RECTANGLE;

    @Min(value = 2, message = "wallCount must be greater than or equal to 2")
    @Column(name = "wall_count", nullable = false)
    @Builder.Default
    private Integer wallCount = 4;

    @Column(name = "width", precision = 10, scale = 2)
    private BigDecimal width;

    @Column(name = "length", precision = 10, scale = 2)
    private BigDecimal length;

    @Column(name = "height", precision = 10, scale = 2)
    private BigDecimal height;

    @DecimalMin(value = "0.0", inclusive = true, message = "positionX must be greater than or equal to 0")
    @Column(name = "position_x", precision = 10, scale = 2)
    private BigDecimal positionX;

    @DecimalMin(value = "0.0", inclusive = true, message = "positionY must be greater than or equal to 0")
    @Column(name = "position_y", precision = 10, scale = 2)
    private BigDecimal positionY;

    @Column(name = "door_count")
    private Integer doorCount;

    @Column(name = "window_count")
    private Integer windowCount;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonBackReference
    private Project project;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("room-elements")
    @Builder.Default
    private List<ConstructionElement> elements = new ArrayList<>();

    @PrePersist
    @PreUpdate
    private void applyShapeDefaults() {
        if (shapeType == null) {
            shapeType = RoomShapeType.RECTANGLE;
        }

        if (wallCount == null) {
            wallCount = defaultWallCountFor(shapeType);
        }
    }

    private int defaultWallCountFor(RoomShapeType type) {
        return type == RoomShapeType.RECTANGLE ? 4 : 3;
    }

    // Convenience helpers to keep both sides in sync
    public void addElement(ConstructionElement element) {
        elements.add(element);
        element.setRoom(this);
    }

    public void removeElement(ConstructionElement element) {
        elements.remove(element);
        element.setRoom(null);
    }
}
