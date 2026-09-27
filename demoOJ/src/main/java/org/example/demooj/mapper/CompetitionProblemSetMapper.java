package org.example.demooj.mapper;

import org.example.demooj.entity.CompetitionProblemSet;
import org.example.demooj.entity.Problem;

import java.util.List;

public interface CompetitionProblemSetMapper {
    //add比赛问题
    Integer insertProblem(CompetitionProblemSet competitionProblemSet);
    //根据比赛ID查问题集
    List<CompetitionProblemSet> selectProblemsByCompetitionId(Integer competitionId);
    //根据比赛ID 问题ID移除问题
    Integer deleteProblem(Integer problemId, Integer competitionId);
    //根据比赛ID 移除问题
    Integer deleteProblemByGameId(Integer competitionId);
    //根据问题ID 移除问题
    Integer deleteProblemByProId(Integer problemId);
    //根据比赛ID 问题ID submit++
    Integer submitSumIncrement(Integer problemId, Integer competitionId);
    //根据比赛ID 问题ID ac++
    Integer acceptSumIncrement(Integer problemId, Integer competitionId);
}
