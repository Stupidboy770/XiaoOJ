package org.example.demooj.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RankProblem {
    Integer beforeAcSubmit;
    boolean isAc;
    boolean isFirst;
    LocalDateTime acTime;
}
