package com.logshield.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scan_finding", indexes = {
        @Index(name = "idx_scan_finding_job_id", columnList = "scan_job_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanFinding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scan_job_id", nullable = false)
    private ScanJob scanJob;

    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    @Enumerated(EnumType.STRING)
    private FindingCategory category;

    @Column(nullable = false)
    private String matchedValue;

    @Column(nullable = false)
    private String redactedValue;

    private Integer lineNumber;

    @Column(columnDefinition = "VARCHAR(50)")
    @Enumerated(EnumType.STRING)
    private FindingSeverity severity;

    public enum FindingCategory {
        EMAIL, IP_ADDRESS, API_KEY, JWT_TOKEN, CREDIT_CARD, DB_CONNECTION_STRING
    }

    public enum FindingSeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
