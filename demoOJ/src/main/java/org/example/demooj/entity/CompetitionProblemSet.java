package org.example.demooj.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompetitionProblemSet {
    Integer id;
    //比赛ID
    Integer competitionId;
    //题目
    Integer problemId;
    //提交总数
    Integer submitSum;
    //ac总数
    Integer acceptSum;
    //题目name
    String problemName;
}
