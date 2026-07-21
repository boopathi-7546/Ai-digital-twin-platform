package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.exception.BadRequestException;
import com.digitaltwin.platform.service.ResumeParserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Extracts text from PDF (via Apache PDFBox) and DOCX (via Apache POI)
 * resume files. Legacy .doc is not supported (Apache POI's HWPF path
 * is brittle) — the frontend should nudge users toward PDF/DOCX.
 */
@Slf4j
@Service
public class ResumeParserServiceImpl implements ResumeParserService {

    private static final int MAX_TEXT_LENGTH = 50_000; // guard against pathological inputs sent to the AI

    @Override
    public String extractText(InputStream inputStream, String fileExtension) {
        try {
            byte[] bytes = readAllBytes(inputStream);
            String text = switch (fileExtension.toLowerCase()) {
                case "pdf" -> extractFromPdf(bytes);
                case "docx" -> extractFromDocx(bytes);
                default -> throw new BadRequestException(
                        "Unsupported resume format for text extraction: " + fileExtension);
            };

            if (text == null || text.isBlank()) {
                throw new BadRequestException(
                        "Could not extract any readable text from this resume. It may be a scanned image without OCR.");
            }

            return text.length() > MAX_TEXT_LENGTH ? text.substring(0, MAX_TEXT_LENGTH) : text;
        } catch (IOException ex) {
            log.error("Failed to parse resume file: {}", ex.getMessage());
            throw new BadRequestException("Failed to read the uploaded resume file.");
        }
    }

    private String extractFromPdf(byte[] bytes) throws IOException {
        try (PDDocument document = Loader.loadPDF(bytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        }
    }

    private String extractFromDocx(byte[] bytes) throws IOException {
        try (XWPFDocument document = new XWPFDocument(new java.io.ByteArrayInputStream(bytes));
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        inputStream.transferTo(buffer);
        return buffer.toByteArray();
    }
}
