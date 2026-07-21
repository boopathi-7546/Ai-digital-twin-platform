package com.digitaltwin.platform.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Uniform error response body returned by GlobalExceptionHandler for
 * every failure case, so the frontend can rely on one consistent shape.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    @Builder.Default
    private String timestamp = Instant.now().toString();

    private int status;
    private String error;
    private String message;
    private String path;

    /** Field -> validation error message, populated only for 400s from @Valid failures. */
    private Map<String, String> fieldErrors;

    /** Optional list of general (non-field-specific) error messages. */
    private List<String> details;
}
