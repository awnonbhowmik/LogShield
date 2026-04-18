package com.logshield.backend.controller;

import com.logshield.backend.dto.PagedScanResponse;
import com.logshield.backend.dto.ScanDetailResponse;
import com.logshield.backend.dto.ScanUploadResponse;
import com.logshield.backend.service.ScanService;
import com.logshield.backend.validation.FileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/scans")
@RequiredArgsConstructor
public class ScanController {

    private final ScanService scanService;
    private final FileValidator fileValidator;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ScanUploadResponse> uploadScan(
            @RequestParam("file") MultipartFile file) {

        fileValidator.validate(file);
        return ResponseEntity.ok(scanService.processScan(file));
    }

    @GetMapping
    public ResponseEntity<PagedScanResponse> getAllScans(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(100, Math.max(1, size));
        return ResponseEntity.ok(scanService.getScansPage(search, safePage, safeSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScanDetailResponse> getScanById(@PathVariable Long id) {
        return ResponseEntity.ok(scanService.getScanById(id));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadRedactedFile(@PathVariable Long id) {
        ScanDetailResponse detail = scanService.getScanById(id);
        byte[] content = scanService.getRedactedFile(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"redacted_" + new File(detail.filename()).getName() + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(content);
    }
}
