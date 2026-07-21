package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.dto.admin.SkillRequest;
import com.digitaltwin.platform.dto.admin.SkillResponse;
import com.digitaltwin.platform.entity.Skill;
import com.digitaltwin.platform.exception.BadRequestException;
import com.digitaltwin.platform.exception.ResourceNotFoundException;
import com.digitaltwin.platform.repository.SkillRepository;
import com.digitaltwin.platform.service.AdminSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminSkillServiceImpl implements AdminSkillService {

    private final SkillRepository skillRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SkillResponse createSkill(SkillRequest request) {
        if (skillRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("A skill named '" + request.getName() + "' already exists.");
        }

        Skill skill = Skill.builder()
                .name(request.getName())
                .category(request.getCategory())
                .description(request.getDescription())
                .active(request.isActive())
                .build();

        skillRepository.save(skill);
        return toResponse(skill);
    }

    @Override
    @Transactional
    public SkillResponse updateSkill(Long skillId, SkillRequest request) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Skill", skillId));

        skill.setName(request.getName());
        skill.setCategory(request.getCategory());
        skill.setDescription(request.getDescription());
        skill.setActive(request.isActive());

        skillRepository.save(skill);
        return toResponse(skill);
    }

    @Override
    @Transactional
    public void deleteSkill(Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Skill", skillId));
        skillRepository.delete(skill);
    }

    private SkillResponse toResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .category(skill.getCategory())
                .description(skill.getDescription())
                .active(skill.isActive())
                .build();
    }
}
