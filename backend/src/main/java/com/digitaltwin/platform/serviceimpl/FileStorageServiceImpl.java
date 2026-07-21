package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.config.FileStorageConfig;
import com.digitaltwin.platform.exception.BadRequestException;
import com.digitaltwin.platform.exception.ResourceNotFoundException;
import com.digitaltwin.platform.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Stores files on the local filesystem under app.file-storage.upload-dir.
 * Filenames are randomized (UUID + timestamp) to avoid collisions and
 * to prevent path-based enumeration of other users' files.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final FileStorageConfig fileStorageConfig;

    private static final List<String> ALLOWED_RESUME_EXTENSIONS = List.of("pdf", "doc", "docx");
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    public String store(MultipartFile file, String subDirectory) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file is empty.");
        }

        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        String extension = getExtension(originalFilename);

        if ("resumes".equals(subDirectory) && !ALLOWED_RESUME_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException("Only PDF, DOC, and DOCX files are supported for resumes.");
        }

        if (originalFilename.contains("..")) {
            throw new BadRequestException("Filename contains invalid path sequence: " + originalFilename);
        }

        String storedFilename = UUID.randomUUID() + "_" + LocalDateTime.now().format(TS_FORMAT)
                + (extension.isEmpty() ? "" : "." + extension);

        try {
            Path targetDir = fileStorageConfig.resolveRoot().resolve(subDirectory);
            Files.createDirectories(targetDir);
            Path targetPath = targetDir.resolve(storedFilename).normalize();

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String relativePath = subDirectory + "/" + storedFilename;
            log.info("Stored file '{}' as '{}'", originalFilename, relativePath);
            return relativePath;
        } catch (IOException ex) {
            throw new BadRequestException("Could not store file '" + originalFilename + "': " + ex.getMessage());
        }
    }

    @Override
    public String storeBytes(byte[] content, String subDirectory, String suggestedFileName) {
        String cleanName = StringUtils.cleanPath(suggestedFileName == null ? "file" : suggestedFileName);
        String extension = getExtension(cleanName);
        String storedFilename = UUID.randomUUID() + "_" + LocalDateTime.now().format(TS_FORMAT)
                + (extension.isEmpty() ? "" : "." + extension);

        try {
            Path targetDir = fileStorageConfig.resolveRoot().resolve(subDirectory);
            Files.createDirectories(targetDir);
            Path targetPath = targetDir.resolve(storedFilename).normalize();

            Files.write(targetPath, content);

            String relativePath = subDirectory + "/" + storedFilename;
            log.info("Stored generated file as '{}'", relativePath);
            return relativePath;
        } catch (IOException ex) {
            throw new BadRequestException("Could not store generated file: " + ex.getMessage());
        }
    }

    @Override
    public Resource loadAsResource(String relativePath) {
        try {
            Path filePath = fileStorageConfig.resolveRoot().resolve(relativePath).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw ResourceNotFoundException.forEntity("File", relativePath);
        } catch (MalformedURLException ex) {
            throw ResourceNotFoundException.forEntity("File", relativePath);
        }
    }

    @Override
    public void delete(String relativePath) {
        try {
            Path filePath = fileStorageConfig.resolveRoot().resolve(relativePath).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            log.warn("Could not delete file at {}: {}", relativePath, ex.getMessage());
        }
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1 || dotIndex == filename.length() - 1) ? "" : filename.substring(dotIndex + 1);
    }
}
