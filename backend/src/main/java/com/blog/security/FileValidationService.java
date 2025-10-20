package com.blog.security;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.blog.exceptions.InvalidFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class FileValidationService {

    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024;

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp");

    private static final Set<String> ALLOWED_VIDEO_EXTENSIONS = Set.of(
            "mp4", "webm");

    private static final Set<String> BLOCKED_EXTENSIONS = Set.of(
            "svg", "svgz",
            "html", "htm", "xhtml",
            "php", "jsp", "asp", "aspx",
            "js", "exe", "bat", "sh",
            "xml", "xsl", "xslt"
    );

    public void validateFile(MultipartFile file) throws InvalidFormatException {
        if (file == null || file.isEmpty()) {
            throw new InvalidFormatException("File is empty or null");
        }
        validateFileSize(file);
        String sanitizedFilename = sanitizeFilename(file.getOriginalFilename());
        String extension = getFileExtension(sanitizedFilename);
        validateExtension(extension);
        validateMimeType(file.getContentType(), extension);

        try {
            validateFileContent(file, extension);
        } catch (IOException e) {
            throw new InvalidFormatException("Failed to read file content: " + e.getMessage());
        }
    }

    private void validateFileSize(MultipartFile file) throws InvalidFormatException {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFormatException(
                    String.format("File size exceeds maximum allowed size of %d MB",
                            MAX_FILE_SIZE / (1024 * 1024)));
        }

        if (file.getSize() == 0) {
            throw new InvalidFormatException("File is empty");
        }
    }

    public String sanitizeFilename(String filename) throws InvalidFormatException {
        if (filename == null || filename.trim().isEmpty()) {
            throw new InvalidFormatException("Filename is null or empty");
        }

        filename = filename.replace("\u0000", "");
        filename = filename.replaceAll("\\.\\.", "");
        filename = filename.replaceAll("[/\\\\]", "");
        filename = filename.replaceAll("[^\\x20-\\x7E]", "");
        filename = filename.trim();

        if (filename.isEmpty()) {
            throw new InvalidFormatException("Filename is invalid after sanitization");
        }

        return filename;
    }

    private String getFileExtension(String filename) throws InvalidFormatException {
        if (filename == null || !filename.contains(".")) {
            throw new InvalidFormatException("File has no extension");
        }

        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

        if (extension.isEmpty()) {
            throw new InvalidFormatException("File extension is empty");
        }

        return extension;
    }

    private void validateExtension(String extension) throws InvalidFormatException {
        if (BLOCKED_EXTENSIONS.contains(extension)) {
            throw new InvalidFormatException(
                    String.format("File extension '.%s' is not allowed for security reasons", extension));
        }

        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension) &&
                !ALLOWED_VIDEO_EXTENSIONS.contains(extension)) {
            throw new InvalidFormatException(
                    String.format("File extension '.%s' is not supported. Allowed: %s, %s",
                            extension,
                            ALLOWED_IMAGE_EXTENSIONS,
                            ALLOWED_VIDEO_EXTENSIONS));
        }
    }

    private void validateMimeType(String mimeType, String extension) throws InvalidFormatException {
        if (mimeType == null || mimeType.trim().isEmpty()) {
            throw new InvalidFormatException("MIME type is null or empty");
        }
        System.out.println("MimeType================================>>>>> " + mimeType);
        mimeType = mimeType.split(";")[0].trim().toLowerCase();
        if (!mimeType.contains("/")) {
            throw new InvalidFormatException("Invalid MIME type format: " + mimeType);
        }
        boolean isValidMime = false;
        if (ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            isValidMime = mimeType.startsWith("image/");
            if (isValidMime) {
                isValidMime = mimeTypeMatchesExtension(mimeType, extension);
            }
        } else if (ALLOWED_VIDEO_EXTENSIONS.contains(extension)) {
            isValidMime = mimeType.startsWith("video/");
            if (isValidMime) {
                isValidMime = mimeTypeMatchesExtension(mimeType, extension);
            }
        }

        if (!isValidMime) {
            throw new InvalidFormatException(
                    String.format("MIME type '%s' does not match file extension '.%s'",
                            mimeType, extension));
        }
    }

    private boolean mimeTypeMatchesExtension(String mimeType, String extension) {
        return switch (extension) {
            case "jpg", "jpeg" -> mimeType.equals("image/jpeg");
            case "png" -> mimeType.equals("image/png");
            case "gif" -> mimeType.equals("image/gif");
            case "webp" -> mimeType.equals("image/webp");
            case "mp4" -> mimeType.equals("video/mp4");
            case "webm" -> mimeType.equals("video/webm");
            default -> false;
        };
    }

    private void validateFileContent(MultipartFile file, String extension) throws IOException, InvalidFormatException {
        byte[] fileBytes = new byte[12];
        int bytesRead;
        try (InputStream inputStream = file.getInputStream()) {
            bytesRead = inputStream.read(fileBytes);
        }
        if (bytesRead < 4) {
            throw new InvalidFormatException("File is too small or corrupted");
        }

        scanForMaliciousContent(file);
    }

    private void scanForMaliciousContent(MultipartFile file) throws IOException, InvalidFormatException {
        byte[] content = file.getBytes();
        String contentAsString = new String(content, 0, Math.min(content.length, 1024)).toLowerCase();

        String[] maliciousPatterns = {
                "<script", "javascript:", "onerror=", "onload=",
                "onclick=", "<?php", "<%", "#!/", "eval(",
                "<iframe", "<embed", "<object"
        };

        for (String pattern : maliciousPatterns) {
            if (contentAsString.contains(pattern)) {
                throw new InvalidFormatException(
                        "File contains potentially malicious content: " + pattern);
            }
        }
    }

    public boolean isAllowedImageType(String extension) {
        return extension != null && ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase());
    }

    public boolean isAllowedVideoType(String extension) {
        return extension != null && ALLOWED_VIDEO_EXTENSIONS.contains(extension.toLowerCase());
    }
}
