package com.digitaltwin.platform.repository;

import com.digitaltwin.platform.entity.AiPrompt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiPromptRepository extends JpaRepository<AiPrompt, Long> {

    Optional<AiPrompt> findByPromptKeyAndActiveTrue(String promptKey);

    Optional<AiPrompt> findByPromptKey(String promptKey);
}
