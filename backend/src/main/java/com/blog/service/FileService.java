package com.blog.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {
    
    @Value("${media.upload-dir}")
    private String uploadDir;

    /**
     * Delete a file by its path relative to the upload directory
     * @param filePath relative path from uploads directory (e.g., "posts/image.jpg")
     * @return true if file was deleted, false otherwise
     */
    public boolean deleteFile(String filePath) {
        try {
            if (filePath == null || filePath.isEmpty()) {
                return false;
            }

            // Sanitize path to prevent directory traversal attacks
            String sanitizedPath = filePath.replace("..", "").replace("//", "/");
            
            Path fullPath = Paths.get(uploadDir, sanitizedPath);
            
            // Ensure the path is within the upload directory
            if (!fullPath.normalize().startsWith(Paths.get(uploadDir).normalize())) {
                return false;
            }

            File file = fullPath.toFile();
            if (file.exists() && file.isFile()) {
                return file.delete();
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error deleting file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if a file exists
     * @param filePath relative path from uploads directory
     * @return true if file exists
     */
    public boolean fileExists(String filePath) {
        try {
            if (filePath == null || filePath.isEmpty()) {
                return false;
            }

            String sanitizedPath = filePath.replace("..", "").replace("//", "/");
            Path fullPath = Paths.get(uploadDir, sanitizedPath);
            
            if (!fullPath.normalize().startsWith(Paths.get(uploadDir).normalize())) {
                return false;
            }

            return Files.exists(fullPath);
        } catch (Exception e) {
            return false;
        }
    }
}
