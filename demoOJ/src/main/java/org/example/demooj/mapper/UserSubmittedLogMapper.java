package org.example.demooj.mapper;

import org.example.demooj.entity.UserSubmittedLog;

import java.util.List;

public interface UserSubmittedLogMapper {

    //根据比赛ID 用户ID查询状态
    List<UserSubmittedLog> selectOneUserSubmittedLog(String userId,Integer competitionId);
    //添加一条记录
    Integer insertUserSubmittedLog(UserSubmittedLog userSubmittedLog);
    //根据log_ID主键的状态更改
    Integer updateStatus(Integer id,Long memory,Long runTime,String result);
    //根据比赛ID删记录
    Integer deleteByCompetitionId(Integer competitionId);
    //根据题目ID删记录
    Integer deleteByProblemId(Integer problemId);
    //获取一场比赛的提交数量
    Integer getCount(Integer competitionId,String userId);
    //根据用户ID左右**模糊匹配 or题目ID or 语言
    List<UserSubmittedLog> selectUnique(Integer competitionId,String userId,Integer problemId,String language,Integer offset,Integer limit);
    //查询总数量
    Integer selectCount(Integer competitionId,String userId,Integer problemId,String language);
}
