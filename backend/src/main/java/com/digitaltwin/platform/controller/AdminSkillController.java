package com.digitaltwin.platform.controller;

import com.digitaltwin.platform.dto.admin.SkillRequest;
import com.digitaltwin.platform.dto.admin.SkillResponse;
import com.digitaltwin.platform.service.AdminSkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/skills")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Skills", description = "Manage the master skills catalog")
public class AdminSkillController {

    private final AdminSkillService adminSkillService;

    @GetMapping
    @Operation(summary = "List all skills in the catalog")
    public ResponseEntity<List<SkillResponse>> getAllSkills() {
        return ResponseEntity.ok(adminSkillService.getAllSkills());
    }

    @PostMapping
    @Operation(summary = "Add a new skill to the catalog")
    public ResponseEntity<SkillResponse> createSkill(@Valid @RequestBody SkillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminSkillService.createSkill(request));
    }

    @PutMapping("/{skillId}")
    @Operation(summary = "Update an existing skill")
    public ResponseEntity<SkillResponse> updateSkill(
            @PathVariable Long skillId, @Valid @RequestBody SkillRequest request) {
        return ResponseEntity.ok(adminSkillService.updateSkill(skillId, request));
    }

    @DeleteMapping("/{skillId}")
    @Operation(summary = "Delete a skill from the catalog")
    public ResponseEntity<Map<String, String>> deleteSkill(@PathVariable Long skillId) {
        adminSkillService.deleteSkill(skillId);
        return ResponseEntity.ok(Map.of("message", "Skill deleted successfully."));
    }
}
