package org.example.demooj.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JudgeResult {
    private long memory;
    private long time;
    private ProblemStatus problemStatus;
}
