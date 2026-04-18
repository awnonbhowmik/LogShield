package com.logshield.backend.service;

import com.logshield.backend.dto.PagedScanResponse;
import com.logshield.backend.dto.ScanDetailResponse;
import com.logshield.backend.dto.ScanSummaryResponse;
import com.logshield.backend.dto.ScanUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ScanService {

    ScanUploadResponse processScan(MultipartFile file);

    List<ScanSummaryResponse> getAllScans();

    PagedScanResponse getScansPage(String search, int page, int size);

    ScanDetailResponse getScanById(Long id);

    byte[] getRedactedFile(Long id);
}
