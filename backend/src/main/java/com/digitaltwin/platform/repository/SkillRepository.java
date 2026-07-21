package com.digitaltwin.platform.repository;

import com.digitaltwin.platform.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Skill> findByActiveTrue();

    List<Skill> findByCategoryIgnoreCaseAndActiveTrue(String category);
}
