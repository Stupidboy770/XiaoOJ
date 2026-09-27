package org.example.demooj.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.example.demooj.entity.RankInfo;
import org.example.demooj.entity.RankResult;
import org.example.demooj.entity.User;
import org.example.demooj.mapper.RankMapper;
import org.example.demooj.mapper.UserMapper;
import org.example.demooj.service.RankEnd;
import org.example.demooj.service.StatusAndRank;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class RankEndImpl implements RankEnd {

    @Resource
    private StatusAndRank  statusAndRank;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private RankMapper rankMapper;
    @Resource
    private UserMapper userMapper;

    @Override
    public void toMysql(Integer competitionId) {
        List<RankResult> ranks=statusAndRank.getRank(competitionId,0,100000);
        String info="{}";
        try{
            info=objectMapper.writeValueAsString(ranks);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        rankMapper.addRank(competitionId,info);
    }

    @Override
    public Integer deleteRank(Integer competitionId) {
        return rankMapper.deleteRank(competitionId);
    }

    @Override
    public List<RankResult> getRank(Integer competitionId,Integer start,Integer end) {
        RankInfo rankInfo=rankMapper.getRankById(competitionId);
        if (rankInfo == null || rankInfo.getRankInfo() == null) {
            return Collections.emptyList();
        }
        List<RankResult> rankResultList;
        try{
            rankResultList=objectMapper.readValue(rankInfo.getRankInfo(),new TypeReference<List<RankResult>>(){});
        }catch(Exception e){
            System.out.println(e.getMessage());
            return Collections.emptyList();
        }
        try{

            return rankResultList.subList(start,end);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public RankResult getRankResult(Integer competitionId, String userId) {
        RankInfo rankInfo=rankMapper.getRankById(competitionId);
        String userName=userMapper.getUserById(userId).getName();
        if (rankInfo == null || rankInfo.getRankInfo() == null) {
            return null;
        }
        List<RankResult> rankResultList;
        try{
            rankResultList=objectMapper.readValue(rankInfo.getRankInfo(),new TypeReference<List<RankResult>>(){});
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
        for (RankResult rankResult : rankResultList) {
            if(rankResult.getUserName().equals(userName)){
                return rankResult;
            }
        }
        return null;
    }

    @Resource
    private ThreadPoolTaskScheduler scheduler;

    private final Map<Integer, ScheduledFuture<?>> futureMap = new ConcurrentHashMap<>();

    @Override
    public void startRank(Date endTime, Integer competitionId) {

        ScheduledFuture<?> future = scheduler.schedule(()->{
            try{
                toMysql(competitionId);
                statusAndRank.clearDatabase(competitionId);
            }catch(Exception e){
                System.out.println(e.getMessage());
                System.out.println("排行榜导出失败");
            }finally {
                futureMap.remove(competitionId);
            }
        },endTime);

        futureMap.put(competitionId, future);

    }

    @Override
    public void cancelRank(Integer competitionId) {
        ScheduledFuture<?> future = futureMap.get(competitionId);
        if (future != null && !future.isDone()) {
            future.cancel(false);
            futureMap.remove(competitionId);
        }
    }
}
