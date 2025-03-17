package com.wanderai.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tour_packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @NotBlank
    private String title;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    private Double price;

    @NotNull
    private Double originalPrice;

    @NotBlank
    private String location;

    @NotBlank
    private String duration;

    @ElementCollection
    @CollectionTable(name = "package_features")
    @Column(name = "feature")
    private List<String> features = new ArrayList<>();

    private String status;

    private Integer soldCount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "tourPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PackageItinerary> itinerary = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "package_includes")
    @Column(name = "include")
    private List<String> includes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "package_excludes")
    @Column(name = "exclude")
    private List<String> excludes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "package_notes")
    @Column(name = "note")
    private List<String> notes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (soldCount == null) {
            soldCount = 0;
        }
    }
}