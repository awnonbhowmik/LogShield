package com.logshield.backend.service;

import com.logshield.backend.dto.ScanDetailResponse;
import com.logshield.backend.dto.ScanSummaryResponse;
import com.logshield.backend.dto.ScanUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ScanService {

    ScanUploadResponse processScan(MultipartFile file);

    List<ScanSummaryResponse> getAllScans();

    ScanDetailResponse getScanById(Long id);

    byte[] getRedactedFile(Long id);
}
