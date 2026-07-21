package com.digitaltwin.platform.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Binds app.file-storage.* properties and ensures the base upload
 * directory (and its subdirectories) exist on startup.
 */
@Slf4j
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.file-storage")
public class FileStorageConfig {

    private String uploadDir;
    private int maxResumeSizeMb;

    @PostConstruct
    public void init() {
        try {
            Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(root);
            Files.createDirectories(root.resolve("resumes"));
            Files.createDirectories(root.resolve("profile-pictures"));
            Files.createDirectories(root.resolve("reports"));
            log.info("File storage initialized at {}", root);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not initialize file storage directory: " + uploadDir, ex);
        }
    }

    public Path resolveRoot() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }
}
