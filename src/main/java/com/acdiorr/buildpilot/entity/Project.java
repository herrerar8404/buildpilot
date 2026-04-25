package com.acdiorr.buildpilot.entity;

import com.acdiorr.buildpilot.entity.enums.ProjectStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Project name is required")
    @Column(name = "project_name", nullable = false)
    private String projectName;

    @NotBlank(message = "Client name is required")
    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "construction_type")
    private String constructionType;

    @Column(name = "land_width", precision = 10, scale = 2)
    private BigDecimal landWidth;

    @Column(name = "land_length", precision = 10, scale = 2)
    private BigDecimal landLength;

    @Column(name = "address")
    private String address;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @NotNull(message = "Project status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatus status;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private List<Room> rooms = new ArrayList<>();

    // Convenience helper to keep both sides in sync
    public void addRoom(Room room) {
        rooms.add(room);
        room.setProject(this);
    }

    public void removeRoom(Room room) {
        rooms.remove(room);
        room.setProject(null);
    }
}

