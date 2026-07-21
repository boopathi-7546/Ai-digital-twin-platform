package com.digitaltwin.platform.repository;

import com.digitaltwin.platform.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByStudentIdOrderByGeneratedAtDesc(Long studentId);

    List<Report> findAllByOrderByGeneratedAtDesc();
}
