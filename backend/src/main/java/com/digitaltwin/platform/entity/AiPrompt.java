package com.digitaltwin.platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Admin-managed AI prompt templates keyed by a stable prompt_key
 * (e.g. RESUME_ANALYSIS). Templates use {{placeholder}} tokens that
 * services fill in before calling Gemini — this lets admins tune
 * prompt wording without a code deploy.
 */
@Entity
@Table(name = "ai_prompts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiPrompt {

    public static final String RESUME_ANALYSIS = "RESUME_ANALYSIS";
    public static final String DIGITAL_TWIN_BEHAVIOR = "DIGITAL_TWIN_BEHAVIOR";
    public static final String INTERVIEW_QUESTION_GENERATION = "INTERVIEW_QUESTION_GENERATION";
    public static final String INTERVIEW_FEEDBACK = "INTERVIEW_FEEDBACK";
    public static final String SKILL_GAP_ANALYSIS = "SKILL_GAP_ANALYSIS";
    public static final String ROADMAP_GENERATION = "ROADMAP_GENERATION";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prompt_key", nullable = false, unique = true, length = 100)
    private String promptKey;

    @Lob
    @Column(name = "prompt_template", nullable = false, columnDefinition = "LONGTEXT")
    private String promptTemplate;

    @Column(length = 255)
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}
