package com.digitaltwin.platform.repository;

import com.digitaltwin.platform.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificationRepository extends JpaRepository<Certification, Long> {

    List<Certification> findByStudentIdOrderByIssueDateDesc(Long studentId);

    Optional<Certification> findByIdAndStudentId(Long id, Long studentId);
}
