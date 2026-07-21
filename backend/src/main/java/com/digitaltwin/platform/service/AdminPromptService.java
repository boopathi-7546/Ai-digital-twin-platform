package com.digitaltwin.platform.service;

import com.digitaltwin.platform.dto.admin.AiPromptRequest;
import com.digitaltwin.platform.dto.admin.AiPromptResponse;

import java.util.List;

/**
 * Admin management of AI prompt templates (see AiPrompt entity).
 * Prompt keys are fixed (defined as constants on AiPrompt) — admins
 * edit the template text/description/active flag, not the key itself.
 */
public interface AdminPromptService {

    List<AiPromptResponse> getAllPrompts();

    AiPromptResponse updatePrompt(String promptKey, AiPromptRequest request, String updatedByEmail);
}
