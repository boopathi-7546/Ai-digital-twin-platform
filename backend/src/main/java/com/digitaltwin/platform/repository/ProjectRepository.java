package com.digitaltwin.platform.repository;

import com.digitaltwin.platform.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByStudentIdOrderByStartDateDesc(Long studentId);

    Optional<Project> findByIdAndStudentId(Long id, Long studentId);
}
