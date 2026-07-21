package com.digitaltwin.platform.repository;

import com.digitaltwin.platform.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EducationRepository extends JpaRepository<Education, Long> {

    List<Education> findByStudentIdOrderByEndYearDesc(Long studentId);

    Optional<Education> findByIdAndStudentId(Long id, Long studentId);
}
