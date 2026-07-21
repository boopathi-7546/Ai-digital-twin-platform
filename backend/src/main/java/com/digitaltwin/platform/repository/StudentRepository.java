package com.digitaltwin.platform.repository;

import com.digitaltwin.platform.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.user WHERE s.id = :id")
    Optional<Student> findByIdWithUser(Long id);
}
