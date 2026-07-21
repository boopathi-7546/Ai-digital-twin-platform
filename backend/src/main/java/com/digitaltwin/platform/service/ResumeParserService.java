package com.digitaltwin.platform.service;

import java.io.InputStream;

/**
 * Extracts raw plain text from an uploaded resume file (PDF or DOCX)
 * so it can be handed to the AI analysis prompt.
 */
public interface ResumeParserService {

    /**
     * @param inputStream the resume file content
     * @param fileExtension lower-case extension without the dot, e.g. "pdf", "docx"
     */
    String extractText(InputStream inputStream, String fileExtension);
}
