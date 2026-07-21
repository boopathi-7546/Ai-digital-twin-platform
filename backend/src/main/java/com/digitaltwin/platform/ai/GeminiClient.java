package com.digitaltwin.platform.ai;
import jakarta.annotation.PostConstruct;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Thin wrapper around the Gemini generateContent REST endpoint.
 * Every AI feature in this platform (resume analysis, digital twin,
 * interview generation/feedback, skill gap, roadmap) goes through this
 * single client so retry/timeout/error handling only lives in one place.
 */
@Slf4j
@Component
public class GeminiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.gemini.api-key}")
    private String apiKey;

    @Value("${app.gemini.api-url}")
    private String apiUrl;

    public GeminiClient(@Value("${app.gemini.timeout-seconds}") int timeoutSeconds) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutSeconds * 1000);
        requestFactory.setReadTimeout(timeoutSeconds * 1000);

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }
    @PostConstruct
public void printConfig() {
    System.out.println("====================================");
    System.out.println("Gemini API URL = " + apiUrl);
    System.out.println("====================================");
}

    /**
     * Sends a single text prompt to Gemini and returns the raw text of
     * the model's response (candidates[0].content.parts[0].text).
     */
    public String generateContent(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "GEMINI_API_KEY is not configured. Set the GEMINI_API_KEY environment variable.");
        }

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        try {
            String rawResponse = restClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            return extractTextFromResponse(rawResponse);
        } catch (Exception ex) {
            log.error("Gemini API call failed: {}", ex.getMessage());
            throw new GeminiApiException("Failed to get a response from the AI service. Please try again shortly.", ex);
        }
    }

    private String extractTextFromResponse(String rawResponse) throws Exception {
        JsonNode root = objectMapper.readTree(rawResponse);
        JsonNode textNode = root
                .path("candidates").path(0)
                .path("content").path("parts").path(0)
                .path("text");

        if (textNode.isMissingNode()) {
            throw new GeminiApiException("Gemini response did not contain the expected content structure.", null);
        }
        return textNode.asText();
    }

    /**
     * Strips ```json / ``` fences that Gemini sometimes wraps structured
     * output in, so callers can pass the result straight to a JSON parser.
     */
    public static String stripJsonFences(String text) {
        String trimmed = text.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceFirst("^```(json)?", "").trim();
            if (trimmed.endsWith("```")) {
                trimmed = trimmed.substring(0, trimmed.length() - 3).trim();
            }
        }
        return trimmed;
    }

    public static class GeminiApiException extends RuntimeException {
        public GeminiApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
