package com.logshield.backend.service;

import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.entity.ScanFinding.FindingSeverity;
import com.logshield.backend.entity.ScanJob;
import com.logshield.backend.entity.ScanJob.ScanStatus;
import com.logshield.backend.exception.ScanNotFoundException;
import com.logshield.backend.repository.ScanJobRepository;
import com.logshield.backend.scanner.DetectionEngine;
import com.logshield.backend.scanner.DetectionResult;
import com.logshield.backend.scanner.RawMatch;
import com.logshield.backend.service.impl.ScanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScanServiceImplTest {

    @Mock ScanJobRepository scanJobRepository;
    @Mock DetectionEngine detectionEngine;

    ScanServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ScanServiceImpl(scanJobRepository, detectionEngine);
    }

    // ── processScan ──────────────────────────────────────────────────────────

    @Test
    void processScan_withFindings_persistsJobAndReturnsResponse() {
        RawMatch match = new RawMatch(
                FindingCategory.EMAIL, FindingSeverity.LOW,
                "user@example.com", "[REDACTED_EMAIL]", 1, 0, 16);
        DetectionResult result = new DetectionResult(
                List.of(match), "redacted content", 1, 5);

        when(detectionEngine.scan(any())).thenReturn(result);
        when(scanJobRepository.save(any())).thenAnswer(inv -> {
            ScanJob job = inv.getArgument(0);
            try {
                var idField = ScanJob.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(job, 1L);
            } catch (Exception ignored) {}
            return job;
        });

        MockMultipartFile file = new MockMultipartFile(
                "file", "server.log", "text/plain", "user@example.com logged in".getBytes());

        var response = service.processScan(file);

        assertThat(response.filename()).isEqualTo("server.log");
        assertThat(response.status()).isEqualTo(ScanStatus.COMPLETED);
        assertThat(response.totalFindings()).isEqualTo(1);
        assertThat(response.severityScore()).isEqualTo(5);
        assertThat(response.findingsByType()).containsKey(FindingCategory.EMAIL);

        ArgumentCaptor<ScanJob> captor = ArgumentCaptor.forClass(ScanJob.class);
        verify(scanJobRepository).save(captor.capture());
        ScanJob saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(ScanStatus.COMPLETED);
        assertThat(saved.getFindings()).hasSize(1);
    }

    @Test
    void processScan_cleanFile_returnsZeroFindingsAndScore() {
        DetectionResult empty = new DetectionResult(List.of(), "clean log", 0, 0);
        when(detectionEngine.scan(any())).thenReturn(empty);
        when(scanJobRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MockMultipartFile file = new MockMultipartFile(
                "file", "clean.log", "text/plain", "clean log".getBytes());

        var response = service.processScan(file);

        assertThat(response.totalFindings()).isZero();
        assertThat(response.severityScore()).isZero();
        assertThat(response.findingsByType()).isEmpty();
    }

    @Test
    void processScan_redactedPreviewTruncatedAt1000Chars() {
        String longContent = "x".repeat(2000);
        DetectionResult result = new DetectionResult(List.of(), longContent, 0, 0);
        when(detectionEngine.scan(any())).thenReturn(result);
        when(scanJobRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MockMultipartFile file = new MockMultipartFile(
                "file", "big.log", "text/plain", "input".getBytes());

        var response = service.processScan(file);

        // preview must be capped — original 2000 chars + ellipsis
        assertThat(response.redactedPreview()).hasSize(1001); // 1000 + "…"
    }

    @Test
    void processScan_blankOriginalFilename_usesDefault() {
        DetectionResult result = new DetectionResult(List.of(), "", 0, 0);
        when(detectionEngine.scan(any())).thenReturn(result);
        when(scanJobRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // MockMultipartFile converts null name to "" — test with blank to verify service fallback
        MockMultipartFile file = new MockMultipartFile(
                "file", "  ", "text/plain", "content".getBytes());

        var response = service.processScan(file);

        assertThat(response.filename()).isEqualTo("unknown.log");
    }

    // ── getAllScans ──────────────────────────────────────────────────────────

    @Test
    void getAllScans_returnsSummariesOrderedByRepository() {
        ScanJob job = ScanJob.builder()
                .filename("app.log").uploadedAt(LocalDateTime.now())
                .status(ScanStatus.COMPLETED).originalSize(512L)
                .severityScore(10).redactedContent("redacted")
                .build();

        when(scanJobRepository.findAllByOrderByUploadedAtDesc()).thenReturn(List.of(job));

        var summaries = service.getAllScans();

        assertThat(summaries).hasSize(1);
        assertThat(summaries.get(0).filename()).isEqualTo("app.log");
        assertThat(summaries.get(0).severityScore()).isEqualTo(10);
    }

    @Test
    void getAllScans_emptyRepository_returnsEmptyList() {
        when(scanJobRepository.findAllByOrderByUploadedAtDesc()).thenReturn(List.of());
        assertThat(service.getAllScans()).isEmpty();
    }

    // ── getScanById ──────────────────────────────────────────────────────────

    @Test
    void getScanById_existingId_returnsDetail() {
        ScanJob job = ScanJob.builder()
                .filename("found.log").uploadedAt(LocalDateTime.now())
                .status(ScanStatus.COMPLETED).originalSize(256L)
                .severityScore(20).redactedContent("redacted text")
                .build();

        when(scanJobRepository.findById(1L)).thenReturn(Optional.of(job));

        var detail = service.getScanById(1L);

        assertThat(detail.filename()).isEqualTo("found.log");
        assertThat(detail.redactedContent()).isEqualTo("redacted text");
        assertThat(detail.findings()).isEmpty();
    }

    @Test
    void getScanById_missingId_throwsScanNotFoundException() {
        when(scanJobRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getScanById(99L))
                .isInstanceOf(ScanNotFoundException.class);
    }

    // ── getRedactedFile ──────────────────────────────────────────────────────

    @Test
    void getRedactedFile_returnsUtf8Bytes() {
        ScanJob job = ScanJob.builder()
                .filename("r.log").uploadedAt(LocalDateTime.now())
                .status(ScanStatus.COMPLETED).originalSize(10L)
                .severityScore(0).redactedContent("hello")
                .build();

        when(scanJobRepository.findById(5L)).thenReturn(Optional.of(job));

        byte[] bytes = service.getRedactedFile(5L);
        assertThat(new String(bytes)).isEqualTo("hello");
    }

    @Test
    void getRedactedFile_nullContent_returnsEmptyByteArray() {
        ScanJob job = ScanJob.builder()
                .filename("r.log").uploadedAt(LocalDateTime.now())
                .status(ScanStatus.COMPLETED).originalSize(10L)
                .severityScore(0).redactedContent(null)
                .build();

        when(scanJobRepository.findById(6L)).thenReturn(Optional.of(job));

        assertThat(service.getRedactedFile(6L)).isEmpty();
    }
}
