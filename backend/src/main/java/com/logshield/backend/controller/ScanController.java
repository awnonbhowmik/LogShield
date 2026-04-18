package com.logshield.backend.controller;

import com.logshield.backend.dto.ScanDetailResponse;
import com.logshield.backend.dto.ScanSummaryResponse;
import com.logshield.backend.dto.ScanUploadResponse;
import com.logshield.backend.service.ScanService;
import com.logshield.backend.validation.FileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/scans")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
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
    public ResponseEntity<List<ScanSummaryResponse>> getAllScans() {
        return ResponseEntity.ok(scanService.getAllScans());
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
                        "attachment; filename=\"redacted_" + detail.filename() + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(content);
    }
}
