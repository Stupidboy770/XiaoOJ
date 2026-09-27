package org.example.demooj.service;

import org.example.demooj.entity.RankResult;
import org.example.demooj.entity.UserSubmittedLog;

import java.time.LocalDateTime;
import java.util.List;

public interface StatusAndRank {
    /*
    *
    * status
    * */
    //根据比赛ID 用户ID查询状态
    List<UserSubmittedLog> selectOneUserSubmittedLog(String RequestUserId,String selectUserId, Integer competitionId,Integer problemId,String language,Integer offset,Integer limit);
    //添加一条记录
    Integer insertUserSubmittedLog(UserSubmittedLog userSubmittedLog);
    //根据log_ID主键的状态更改
    Integer updateStatus(Integer id,Long memory,Long runTime,String result);
    //查询Submit总数
    Integer getSubmitCount(Integer competitionId,String userId);
    //根据比赛ID 用户ID查询数量
    Integer selectOneUserSubmittedCount(String RequestUserId,String selectUserId, Integer competitionId,Integer problemId,String language);
    /*
    *
    * rank
    * */
    //添加所有非管理用户至榜单
    void addUserToRank(Integer competitionId);
    //清空一场比赛的数据
    void clearDatabase(Integer competitionId);
    //操控某人的分数
    void changeScore(Integer competitionId,String userId, Long score);
    //查看是否ac
    boolean checkAc(Integer competitionId,Integer problemId,String userId);
    //设置ac
    void setAc(Integer competitionId, Integer problemId, String userId, LocalDateTime localDateTime);
    //submit++
    void increaseSubmit(Integer competitionId,Integer problemId,String userId);
    //获取submit
    Integer getSubmit(Integer competitionId,Integer problemId,String userId);
    //获取一个范围内的榜单
    List<RankResult> getRank(Integer competitionId, Integer start, Integer end);
    //查询单个人的榜单
    RankResult getRankResult(Integer competitionId, String userId);
    //添加一个题
    void addAProblem(Integer competitionId,Integer problemId);
    //删除一个题
    void delAProblem(Integer competitionId,Integer problemId);
}
