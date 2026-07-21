package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.dto.admin.AiPromptRequest;
import com.digitaltwin.platform.dto.admin.AiPromptResponse;
import com.digitaltwin.platform.entity.AiPrompt;
import com.digitaltwin.platform.exception.ResourceNotFoundException;
import com.digitaltwin.platform.repository.AiPromptRepository;
import com.digitaltwin.platform.service.AdminPromptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminPromptServiceImpl implements AdminPromptService {

    private final AiPromptRepository aiPromptRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AiPromptResponse> getAllPrompts() {
        return aiPromptRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AiPromptResponse updatePrompt(String promptKey, AiPromptRequest request, String updatedByEmail) {
        AiPrompt prompt = aiPromptRepository.findByPromptKey(promptKey)
                .orElseThrow(() -> new ResourceNotFoundException("No prompt template found for key: " + promptKey));

        prompt.setPromptTemplate(request.getPromptTemplate());
        prompt.setDescription(request.getDescription());
        prompt.setActive(request.isActive());
        prompt.setUpdatedBy(updatedByEmail);

        aiPromptRepository.save(prompt);
        log.info("Prompt template '{}' updated by {}", promptKey, updatedByEmail);

        return toResponse(prompt);
    }

    private AiPromptResponse toResponse(AiPrompt prompt) {
        return AiPromptResponse.builder()
                .id(prompt.getId())
                .promptKey(prompt.getPromptKey())
                .promptTemplate(prompt.getPromptTemplate())
                .description(prompt.getDescription())
                .active(prompt.isActive())
                .updatedAt(prompt.getUpdatedAt())
                .updatedBy(prompt.getUpdatedBy())
                .build();
    }
}
