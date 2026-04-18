package com.logshield.backend.validation;

import com.logshield.backend.exception.InvalidFileException;
import com.logshield.backend.exception.UnsupportedFileTypeException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
public class FileValidator {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("txt", "log");
    private static final long MAX_BYTES = 10L * 1024 * 1024; // 10 MB guard (matches multipart limit)

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Uploaded file is empty or missing");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new InvalidFileException("Filename is missing");
        }

        String extension = extractExtension(filename);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new UnsupportedFileTypeException(
                    "File type '." + extension + "' is not supported. Accepted types: .txt, .log"
            );
        }

        if (file.getSize() > MAX_BYTES) {
            throw new InvalidFileException("File size exceeds the 10 MB limit");
        }
    }

    private static String extractExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot < 0 ? "" : filename.substring(dot + 1).toLowerCase();
    }
}
