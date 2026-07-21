package com.digitaltwin.platform.repository;

import com.digitaltwin.platform.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findByStudentIdOrderByUploadedAtDesc(Long studentId);

    Optional<Resume> findByIdAndStudentId(Long id, Long studentId);

    Optional<Resume> findFirstByStudentIdAndActiveTrueOrderByUploadedAtDesc(Long studentId);
}
