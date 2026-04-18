package com.logshield.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logshield.backend.dto.ErrorResponse;
import com.logshield.backend.dto.FindingResponse;
import com.logshield.backend.dto.ScanDetailResponse;
import com.logshield.backend.dto.ScanSummaryResponse;
import com.logshield.backend.dto.ScanUploadResponse;
import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.entity.ScanFinding.FindingSeverity;
import com.logshield.backend.entity.ScanJob.ScanStatus;
import com.logshield.backend.exception.GlobalExceptionHandler;
import com.logshield.backend.exception.ScanNotFoundException;
import com.logshield.backend.service.ScanService;
import com.logshield.backend.validation.FileValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScanController.class)
@Import({FileValidator.class, GlobalExceptionHandler.class})
class ScanControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean  ScanService scanService;

    // ── fixtures ─────────────────────────────────────────────────────────────

    private static final String ENDPOINT = "/api/scans";

    private static final ScanUploadResponse UPLOAD_RESPONSE = new ScanUploadResponse(
            1L, "test.log", LocalDateTime.now(), ScanStatus.COMPLETED,
            30, 2,
            Map.of(
                    FindingCategory.EMAIL,
                    List.of(new FindingResponse(1L, FindingCategory.EMAIL, "a@b.com",
                            "[REDACTED_EMAIL]", 1, FindingSeverity.LOW))
            ),
            "redacted preview text"
    );

    private static MockMultipartFile logFile(String name, byte[] content) {
        return new MockMultipartFile("file", name, MediaType.TEXT_PLAIN_VALUE, content);
    }

    // ── POST /api/scans ───────────────────────────────────────────────────────

    @Test
    void uploadValidLogFile_returns200WithResponse() throws Exception {
        when(scanService.processScan(any())).thenReturn(UPLOAD_RESPONSE);

        MvcResult result = mockMvc.perform(multipart(ENDPOINT)
                        .file(logFile("app.log", "user logged in from 1.2.3.4".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.filename").value("test.log"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.totalFindings").value(2))
                .andExpect(jsonPath("$.severityScore").value(30))
                .andReturn();

        ScanUploadResponse body = objectMapper.readValue(
                result.getResponse().getContentAsString(), ScanUploadResponse.class);
        assertThat(body.findingsByType()).containsKey(FindingCategory.EMAIL);
    }

    @Test
    void uploadValidTxtFile_returns200() throws Exception {
        when(scanService.processScan(any())).thenReturn(UPLOAD_RESPONSE);

        mockMvc.perform(multipart(ENDPOINT)
                        .file(logFile("notes.txt", "clean content".getBytes())))
                .andExpect(status().isOk());
    }

    @Test
    void uploadEmptyFile_returns400() throws Exception {
        mockMvc.perform(multipart(ENDPOINT)
                        .file(logFile("empty.log", new byte[0])))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void uploadUnsupportedExtension_returns415() throws Exception {
        mockMvc.perform(multipart(ENDPOINT)
                        .file(new MockMultipartFile("file", "report.pdf",
                                MediaType.APPLICATION_PDF_VALUE, "pdf content".getBytes())))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.error").value("Unsupported Media Type"));
    }

    @Test
    void uploadNoFileParam_returns400() throws Exception {
        mockMvc.perform(multipart(ENDPOINT))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void uploadWithNoExtension_returns415() throws Exception {
        mockMvc.perform(multipart(ENDPOINT)
                        .file(new MockMultipartFile("file", "noextension",
                                MediaType.TEXT_PLAIN_VALUE, "content".getBytes())))
                .andExpect(status().isUnsupportedMediaType());
    }

    // ── GET /api/scans ────────────────────────────────────────────────────────

    @Test
    void getAllScans_returns200WithList() throws Exception {
        ScanSummaryResponse summary = new ScanSummaryResponse(
                1L, "app.log", LocalDateTime.now(), ScanStatus.COMPLETED, 25, 3);

        when(scanService.getAllScans()).thenReturn(List.of(summary));

        mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].filename").value("app.log"))
                .andExpect(jsonPath("$[0].findingCount").value(3));
    }

    @Test
    void getAllScans_returnsEmptyList() throws Exception {
        when(scanService.getAllScans()).thenReturn(List.of());

        mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ── GET /api/scans/{id} ───────────────────────────────────────────────────

    @Test
    void getScanById_existingId_returns200() throws Exception {
        ScanDetailResponse detail = new ScanDetailResponse(
                1L, "app.log", LocalDateTime.now(), ScanStatus.COMPLETED,
                25, 1024L, "redacted content",
                List.of());

        when(scanService.getScanById(1L)).thenReturn(detail);

        mockMvc.perform(get(ENDPOINT + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.filename").value("app.log"))
                .andExpect(jsonPath("$.redactedContent").value("redacted content"));
    }

    @Test
    void getScanById_nonexistentId_returns404() throws Exception {
        when(scanService.getScanById(99L)).thenThrow(new ScanNotFoundException(99L));

        mockMvc.perform(get(ENDPOINT + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Scan not found: 99"));
    }

    // ── GET /api/scans/{id}/download ──────────────────────────────────────────

    @Test
    void downloadRedactedFile_returns200WithAttachment() throws Exception {
        ScanDetailResponse detail = new ScanDetailResponse(
                1L, "app.log", LocalDateTime.now(), ScanStatus.COMPLETED,
                0, 512L, "clean text", List.of());

        when(scanService.getScanById(1L)).thenReturn(detail);
        when(scanService.getRedactedFile(1L)).thenReturn("clean text".getBytes());

        mockMvc.perform(get(ENDPOINT + "/1/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=\"redacted_app.log\""))
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string("clean text"));
    }

    @Test
    void downloadRedactedFile_nonexistentId_returns404() throws Exception {
        when(scanService.getScanById(42L)).thenThrow(new ScanNotFoundException(42L));

        mockMvc.perform(get(ENDPOINT + "/42/download"))
                .andExpect(status().isNotFound());
    }
}
