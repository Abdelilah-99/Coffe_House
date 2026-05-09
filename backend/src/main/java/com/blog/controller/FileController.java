package com.blog.controller;

import com.blog.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Delete a file from the uploads directory
     * @param filePath the relative path of the file to delete (e.g., "posts/image.jpg")
     * @return ResponseEntity with success or error message
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("File path is required"));
        }

        boolean deleted = fileService.deleteFile(filePath);
        
        if (deleted) {
            return ResponseEntity.ok(new SuccessResponse("File deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("File not found or could not be deleted"));
        }
    }

    /**
     * Check if a file exists
     * @param filePath the relative path of the file to check
     * @return ResponseEntity indicating if file exists
     */
    @GetMapping("/exists")
    public ResponseEntity<?> fileExists(@RequestParam String filePath) {
        boolean exists = fileService.fileExists(filePath);
        return ResponseEntity.ok(new FileExistsResponse(exists));
    }

    // Helper response classes
    public static class SuccessResponse {
        public String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    public static class FileExistsResponse {
        public boolean exists;

        public FileExistsResponse(boolean exists) {
            this.exists = exists;
        }

        public boolean isExists() {
            return exists;
        }

        public void setExists(boolean exists) {
            this.exists = exists;
        }
    }
}
