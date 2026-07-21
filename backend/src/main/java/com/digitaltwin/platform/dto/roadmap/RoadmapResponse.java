package com.digitaltwin.platform.dto.roadmap;

import com.digitaltwin.platform.entity.Roadmap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapResponse {

    private Long id;
    private String targetRole;
    private String title;
    private String description;
    private Roadmap.Status status;
    private LocalDateTime createdAt;
    private List<RoadmapItemResponse> items;
    private int completedItemCount;
    private int totalItemCount;
}
