package org.example.demooj.service;

import org.example.demooj.entity.RankInfo;
import org.example.demooj.entity.RankResult;

import java.util.Date;
import java.util.List;

public interface RankEnd {

    //将Redis转入MySQL中
    void toMysql(Integer competitionId);

    //delete
    Integer deleteRank(Integer competitionId);

    //查询一条
    List<RankResult> getRank(Integer competitionId, Integer start, Integer end);

    //查询单个人的
    RankResult getRankResult(Integer competitionId, String userId);


    //开启转榜
    void startRank(Date endTime,Integer competitionId);

    //取消任务
    void cancelRank(Integer competitionId);
}
