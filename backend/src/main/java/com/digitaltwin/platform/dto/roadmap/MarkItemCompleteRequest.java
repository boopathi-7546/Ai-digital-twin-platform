package com.digitaltwin.platform.dto.roadmap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarkItemCompleteRequest {

    private boolean completed;
}
