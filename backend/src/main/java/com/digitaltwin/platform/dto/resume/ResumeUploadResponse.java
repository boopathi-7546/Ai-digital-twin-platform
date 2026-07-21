package com.digitaltwin.platform.dto.resume;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeUploadResponse {

    private Long resumeId;
    private String fileName;
    private String fileType;
    private Long fileSizeBytes;
    private LocalDateTime uploadedAt;
    private String downloadUrl;
}
