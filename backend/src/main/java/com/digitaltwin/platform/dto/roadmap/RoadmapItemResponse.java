package com.digitaltwin.platform.dto.roadmap;

import com.digitaltwin.platform.entity.RoadmapItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapItemResponse {

    private Long id;
    private RoadmapItem.ItemType itemType;
    private String title;
    private String description;
    private String resourceUrl;
    private int sequenceNo;
    private boolean completed;
    private LocalDateTime completedAt;
}
