package org.example.demooj.mapper;

import org.example.demooj.entity.RankInfo;

public interface RankMapper {

    //增加一条数据
    void addRank(Integer competitionId,String rank);

    //查找数据
    RankInfo getRankById(Integer competitionId);

    //删除数据
    Integer deleteRank(Integer competitionId);
}
