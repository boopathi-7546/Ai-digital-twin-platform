package com.digitaltwin.platform.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Maps to the `digital_twins` table — a single AI-derived behavioral
 * and learning-pattern snapshot per student, regenerated on demand as
 * new resume/interview/activity data comes in.
 */
@Entity
@Table(name = "digital_twins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DigitalTwin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "behavior_profile", columnDefinition = "json")
    private String behaviorProfile;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "learning_pattern", columnDefinition = "json")
    private String learningPattern;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "career_prediction", columnDefinition = "json")
    private String careerPrediction;

    @Column(name = "confidence_index", precision = 5, scale = 2)
    private BigDecimal confidenceIndex;

    @Column(name = "last_generated_at")
    private LocalDateTime lastGeneratedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
