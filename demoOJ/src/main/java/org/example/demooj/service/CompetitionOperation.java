package org.example.demooj.service;

import org.example.demooj.entity.CompetitionInformation;
import org.example.demooj.entity.CompetitionProblemSet;

import java.util.List;

public interface CompetitionOperation {
    //根据ID删除比赛
    Integer deleteCompetitionById(Integer id);
    //根据ID查找比赛
    CompetitionInformation selectCompetitionById(Integer id);
    //遍历比赛
    List<CompetitionInformation> selectAllCompetition();
    //添加一个比赛 NO_ID
    Integer insertCompetition(CompetitionInformation competitionInformation);
    //修改比赛信息 带_ID
    Integer updateCompetition(CompetitionInformation competitionInformation);
    /*
    *
    * 关于题目集的操作
    *
    * */

    //add比赛问题
    Integer insertProblem(Integer problemId, Integer competitionId);
    //根据比赛ID查问题集
    List<CompetitionProblemSet> selectProblemsByCompetitionId(Integer competitionId);
    //根据比赛ID 问题ID移除问题
    Integer deleteProblem(Integer problemId, Integer competitionId);
    //根据比赛ID 问题ID submit++
    Integer submitSumIncrement(Integer problemId, Integer competitionId);
    //根据比赛ID 问题ID ac++
    Integer acceptSumIncrement(Integer problemId, Integer competitionId);

}
