package com.digitaltwin.platform.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Abstraction over local disk storage for uploaded files (resumes,
 * profile pictures). Kept as an interface so a future S3/GCS-backed
 * implementation can be swapped in without touching callers.
 */
public interface FileStorageService {

    /**
     * Stores the file under the given subdirectory (e.g. "resumes") and
     * returns the relative stored path (relative to the upload root).
     */
    String store(MultipartFile file, String subDirectory);

    /**
     * Stores raw generated content (e.g. a report file this service built
     * in-memory) under the given subdirectory and filename, returning the
     * relative stored path.
     */
    String storeBytes(byte[] content, String subDirectory, String suggestedFileName);

    Resource loadAsResource(String relativePath);

    void delete(String relativePath);
}
