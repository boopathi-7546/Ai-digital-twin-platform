package com.digitaltwin.platform.repository;

import com.digitaltwin.platform.entity.QuestionBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {

    List<QuestionBank> findByRoleIgnoreCase(String role);

    List<QuestionBank> findByCategoryIgnoreCase(String category);

    Page<QuestionBank> findAll(Pageable pageable);
}
