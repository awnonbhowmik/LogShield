package com.logshield.backend.service.impl;

import com.logshield.backend.dto.FindingResponse;
import com.logshield.backend.dto.ScanDetailResponse;
import com.logshield.backend.dto.ScanSummaryResponse;
import com.logshield.backend.dto.ScanUploadResponse;
import com.logshield.backend.entity.ScanFinding;
import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.entity.ScanJob;
import com.logshield.backend.entity.ScanJob.ScanStatus;
import com.logshield.backend.exception.InvalidFileException;
import com.logshield.backend.exception.ScanNotFoundException;
import com.logshield.backend.repository.ScanJobRepository;
import com.logshield.backend.scanner.DetectionEngine;
import com.logshield.backend.scanner.DetectionResult;
import com.logshield.backend.scanner.RawMatch;
import com.logshield.backend.service.ScanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScanServiceImpl implements ScanService {

    private static final int PREVIEW_LENGTH = 1000;

    private final ScanJobRepository scanJobRepository;
    private final DetectionEngine detectionEngine;

    @Override
    @Transactional
    public ScanUploadResponse processScan(MultipartFile file) {
        String filename = file.getOriginalFilename() != null
                ? file.getOriginalFilename() : "unknown.log";

        String content;
        try {
            content = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to read file '{}': {}", filename, e.getMessage());
            throw new InvalidFileException("Could not read the uploaded file");
        }

        DetectionResult result = detectionEngine.scan(content);

        ScanJob job = ScanJob.builder()
                .filename(filename)
                .uploadedAt(LocalDateTime.now())
                .status(ScanStatus.COMPLETED)
                .originalSize(file.getSize())
                .severityScore(result.severityScore())
                .redactedContent(result.redactedContent())
                .build();

        for (RawMatch match : result.matches()) {
            ScanFinding finding = ScanFinding.builder()
                    .scanJob(job)
                    .category(match.category())
                    .matchedValue(match.matchedValue())
                    .redactedValue(match.redactedValue())
                    .lineNumber(match.lineNumber())
                    .severity(match.severity())
                    .build();
            job.getFindings().add(finding);
        }

        scanJobRepository.save(job);
        log.info("Scan completed: id={} file='{}' findings={} score={}",
                job.getId(), filename, result.totalFindings(), result.severityScore());

        return buildUploadResponse(job);
    }

    @Override
    public List<ScanSummaryResponse> getAllScans() {
        return scanJobRepository.findAllByOrderByUploadedAtDesc().stream()
                .map(job -> new ScanSummaryResponse(
                        job.getId(),
                        job.getFilename(),
                        job.getUploadedAt(),
                        job.getStatus(),
                        job.getSeverityScore(),
                        job.getFindings().size()
                ))
                .toList();
    }

    @Override
    public ScanDetailResponse getScanById(Long id) {
        ScanJob job = findJobOrThrow(id);
        return new ScanDetailResponse(
                job.getId(),
                job.getFilename(),
                job.getUploadedAt(),
                job.getStatus(),
                job.getSeverityScore(),
                job.getOriginalSize(),
                job.getRedactedContent(),
                job.getFindings().stream().map(this::toFindingResponse).toList()
        );
    }

    @Override
    public byte[] getRedactedFile(Long id) {
        ScanJob job = findJobOrThrow(id);
        String content = job.getRedactedContent();
        return content != null ? content.getBytes(StandardCharsets.UTF_8) : new byte[0];
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private ScanJob findJobOrThrow(Long id) {
        return scanJobRepository.findById(id)
                .orElseThrow(() -> new ScanNotFoundException(id));
    }

    private ScanUploadResponse buildUploadResponse(ScanJob job) {
        Map<FindingCategory, List<FindingResponse>> byType = job.getFindings().stream()
                .collect(Collectors.groupingBy(
                        ScanFinding::getCategory,
                        Collectors.mapping(this::toFindingResponse, Collectors.toList())
                ));

        String preview = truncate(job.getRedactedContent());

        return new ScanUploadResponse(
                job.getId(),
                job.getFilename(),
                job.getUploadedAt(),
                job.getStatus(),
                job.getSeverityScore(),
                job.getFindings().size(),
                byType,
                preview
        );
    }

    private String truncate(String text) {
        if (text == null) return null;
        return text.length() <= PREVIEW_LENGTH
                ? text
                : text.substring(0, PREVIEW_LENGTH) + "…";
    }

    private FindingResponse toFindingResponse(ScanFinding f) {
        return new FindingResponse(
                f.getId(), f.getCategory(), f.getMatchedValue(),
                f.getRedactedValue(), f.getLineNumber(), f.getSeverity()
        );
    }
}
