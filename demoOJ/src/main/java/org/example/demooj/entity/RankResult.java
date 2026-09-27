package org.example.demooj.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankResult {
    Integer rank;
    String userName;
    Integer acSum;
    List<RankProblem>  rankProblems;
}
