package com.acdiorr.buildpilot.entity;

import com.acdiorr.buildpilot.entity.enums.RoomType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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

    @Column(name = "width", precision = 10, scale = 2)
    private BigDecimal width;

    @Column(name = "length", precision = 10, scale = 2)
    private BigDecimal length;

    @Column(name = "height", precision = 10, scale = 2)
    private BigDecimal height;

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

