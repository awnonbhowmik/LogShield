package com.logshield.backend.validation;

import com.logshield.backend.exception.InvalidFileException;
import com.logshield.backend.exception.UnsupportedFileTypeException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileValidatorTest {

    private final FileValidator validator = new FileValidator();

    @Test
    void acceptsValidLogFile() {
        assertThatNoException().isThrownBy(() ->
            validator.validate(file("app.log", "content".getBytes())));
    }

    @Test
    void acceptsValidTxtFile() {
        assertThatNoException().isThrownBy(() ->
            validator.validate(file("notes.txt", "content".getBytes())));
    }

    @Test
    void rejectsEmptyFile() {
        assertThatThrownBy(() -> validator.validate(file("empty.log", new byte[0])))
                .isInstanceOf(InvalidFileException.class);
    }

    @Test
    void rejectsNullFile() {
        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(InvalidFileException.class);
    }

    @Test
    void rejectsUnsupportedExtension() {
        assertThatThrownBy(() -> validator.validate(file("report.pdf", "data".getBytes())))
                .isInstanceOf(UnsupportedFileTypeException.class);
    }

    @Test
    void rejectsFileWithNoExtension() {
        assertThatThrownBy(() -> validator.validate(file("noextension", "data".getBytes())))
                .isInstanceOf(UnsupportedFileTypeException.class);
    }

    @Test
    void rejectsDotOnlyExtension() {
        // filename like "." has no real extension
        assertThatThrownBy(() -> validator.validate(file(".", "data".getBytes())))
                .isInstanceOf(UnsupportedFileTypeException.class);
    }

    @Test
    void acceptsDoubleExtensionWhenLastIsLog() {
        // "archive.backup.log" — last extension is .log, should pass
        assertThatNoException().isThrownBy(() ->
            validator.validate(file("archive.backup.log", "content".getBytes())));
    }

    @Test
    void rejectsDoubleExtensionWhenLastIsUnsupported() {
        // "app.log.zip" — last extension is .zip
        assertThatThrownBy(() -> validator.validate(file("app.log.zip", "data".getBytes())))
                .isInstanceOf(UnsupportedFileTypeException.class);
    }

    @Test
    void rejectsBlankFilename() {
        MockMultipartFile f = new MockMultipartFile("file", "   ", MediaType.TEXT_PLAIN_VALUE, "data".getBytes());
        assertThatThrownBy(() -> validator.validate(f))
                .isInstanceOf(InvalidFileException.class);
    }

    @Test
    void rejectsFileThatExceedsMaxSize() {
        byte[] bigContent = new byte[10 * 1024 * 1024 + 1];
        assertThatThrownBy(() -> validator.validate(file("big.log", bigContent)))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("10 MB");
    }

    private static MockMultipartFile file(String name, byte[] content) {
        return new MockMultipartFile("file", name, MediaType.TEXT_PLAIN_VALUE, content);
    }
}
