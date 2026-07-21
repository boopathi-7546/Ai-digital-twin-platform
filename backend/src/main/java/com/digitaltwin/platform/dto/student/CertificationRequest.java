package com.digitaltwin.platform.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificationRequest {

    @NotBlank(message = "Certification title is required")
    @Size(max = 200)
    private String title;

    @Size(max = 200)
    private String issuingOrganization;

    private LocalDate issueDate;

    private LocalDate expiryDate;

    @Size(max = 150)
    private String credentialId;

    @Size(max = 255)
    private String credentialUrl;
}
