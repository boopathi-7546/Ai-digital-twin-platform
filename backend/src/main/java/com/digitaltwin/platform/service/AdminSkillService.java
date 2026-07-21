package com.digitaltwin.platform.service;

import com.digitaltwin.platform.dto.admin.SkillRequest;
import com.digitaltwin.platform.dto.admin.SkillResponse;

import java.util.List;

/**
 * Admin-managed master skill catalog CRUD (the same Skill entity
 * students reference via StudentSkill).
 */
public interface AdminSkillService {

    List<SkillResponse> getAllSkills();

    SkillResponse createSkill(SkillRequest request);

    SkillResponse updateSkill(Long skillId, SkillRequest request);

    void deleteSkill(Long skillId);
}
