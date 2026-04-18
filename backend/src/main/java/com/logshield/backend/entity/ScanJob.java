package com.logshield.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scan_job")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ScanStatus status;

    private Integer severityScore;

    @Column(columnDefinition = "TEXT")
    private String redactedContent;

    private Long originalSize;

    @OneToMany(mappedBy = "scanJob", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ScanFinding> findings = new ArrayList<>();

    public enum ScanStatus {
        PENDING, COMPLETED, FAILED
    }
}
