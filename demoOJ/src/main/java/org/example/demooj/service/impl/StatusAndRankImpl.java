package org.example.demooj.service.impl;

import jakarta.annotation.Resource;
import org.example.demooj.entity.*;
import org.example.demooj.mapper.CompetitionMapper;
import org.example.demooj.mapper.CompetitionProblemSetMapper;
import org.example.demooj.mapper.UserMapper;
import org.example.demooj.mapper.UserSubmittedLogMapper;
import org.example.demooj.service.LoginValidation;
import org.example.demooj.service.StatusAndRank;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class StatusAndRankImpl implements StatusAndRank {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private UserMapper userMapper;
    @Resource
    CompetitionProblemSetMapper  competitionProblemSetMapper;
    @Resource
    CompetitionMapper competitionMapper;

    @Resource
    private UserSubmittedLogMapper userSubmittedLogMapper;

    @Override
    public Integer insertUserSubmittedLog(UserSubmittedLog userSubmittedLog) {
        return userSubmittedLogMapper.insertUserSubmittedLog(userSubmittedLog);
    }

    @Override
    public List<UserSubmittedLog> selectOneUserSubmittedLog(String RequestUserId,String selectUserId, Integer competitionId,Integer problemId,String language,Integer offset,Integer limit) {
        User user=userMapper.getUserById(RequestUserId);
        CompetitionInformation competitionInformation=competitionMapper.selectCompetitionById(competitionId);
        if(user.isAdmin()||LocalDateTime.now().isAfter(competitionInformation.getEndTime()))return userSubmittedLogMapper.selectUnique(competitionId,selectUserId,problemId,language,offset,limit);
        return userSubmittedLogMapper.selectUnique(competitionId,RequestUserId,problemId,language,offset,limit);
    }

    @Override
    public Integer selectOneUserSubmittedCount(String RequestUserId, String selectUserId, Integer competitionId, Integer problemId, String language) {
        User user=userMapper.getUserById(RequestUserId);
        CompetitionInformation competitionInformation=competitionMapper.selectCompetitionById(competitionId);
        if(user.isAdmin()||LocalDateTime.now().isAfter(competitionInformation.getEndTime()))return userSubmittedLogMapper.selectCount(competitionId,selectUserId,problemId,language);
        return userSubmittedLogMapper.selectCount(competitionId,RequestUserId,problemId,language);
    }

    @Override
    public Integer updateStatus(Integer id, Long memory, Long runTime, String result) {
        return userSubmittedLogMapper.updateStatus(id,memory,runTime,result);
    }

    @Override
    public Integer getSubmitCount(Integer competitionId, String userId) {
        User user=userMapper.getUserById(userId);
        CompetitionInformation competitionInformation=competitionMapper.selectCompetitionById(competitionId);
        if(user.isAdmin()||LocalDateTime.now().isAfter(competitionInformation.getEndTime()))return userSubmittedLogMapper.getCount(competitionId,null);
        return  userSubmittedLogMapper.getCount(competitionId,userId);
    }

    @Override
    public void clearDatabase(Integer competitionId) {
        List<User> users=userMapper.getAllUsers();
        List<CompetitionProblemSet> competitionProblemSets=competitionProblemSetMapper.selectProblemsByCompetitionId(competitionId);
        users.forEach(user -> {
            competitionProblemSets.forEach(p ->
                    redisTemplate.delete(user.getUserId() + ":" + competitionId + ":" + p.getProblemId())
            );
        });
        competitionProblemSets.forEach(p -> {
            redisTemplate.delete("firstBloodPerson:"+competitionId+":"+p.getProblemId());
            redisTemplate.delete("firstBloodTime:"+competitionId+":"+p.getProblemId());
        });
        redisTemplate.delete("rank:"+competitionId);
    }

    @Override
    public void addUserToRank(Integer competitionId) {
        List<User> users=userMapper.getAllUsers();
        List<CompetitionProblemSet> competitionProblemSets= competitionProblemSetMapper.selectProblemsByCompetitionId(competitionId);
        users.forEach(user->{
            redisTemplate.opsForZSet().add("rank:"+competitionId,user.getUserId(),0);
            redisTemplate.opsForValue().set(user.getUserId(),user.getName());
            competitionProblemSets.forEach(p->{
                redisTemplate.opsForHash().put(user.getUserId()+":"+competitionId+":"+p.getProblemId(),"isAc",false);
                redisTemplate.opsForHash().put(user.getUserId()+":"+competitionId+":"+p.getProblemId(),"beforeAcSubmit",0);
            });
        });
    }

    @Override
    public void changeScore(Integer competitionId, String userId, Long score) {
        redisTemplate.opsForZSet().incrementScore("rank:"+competitionId,userId,(double)score);
    }

    @Override
    public boolean checkAc(Integer competitionId, Integer problemId, String userId) {
        return (boolean)redisTemplate.opsForHash().get(userId+":"+competitionId+":"+problemId,"isAc");
    }

    @Override
    public void setAc(Integer competitionId, Integer problemId, String userId, LocalDateTime localDateTime) {
        redisTemplate.opsForHash().put(userId+":"+competitionId+":"+problemId,"isAc",true);
        redisTemplate.opsForHash().put(userId+":"+competitionId+":"+problemId,"acTime",localDateTime);
        if(!redisTemplate.hasKey("firstBloodPerson:"+competitionId+":"+problemId)){
            redisTemplate.opsForValue().set("firstBloodPerson:"+competitionId+":"+problemId,userId);
            redisTemplate.opsForValue().set("firstBloodTime:"+competitionId+":"+problemId,localDateTime);
        }
    }

    @Override
    public void increaseSubmit(Integer competitionId, Integer problemId, String userId) {
        redisTemplate.opsForHash().increment(userId+":"+competitionId+":"+problemId,"beforeAcSubmit",1);
    }

    @Override
    public Integer getSubmit(Integer competitionId, Integer problemId, String userId) {
        return (Integer) redisTemplate.opsForHash().get(userId+":"+competitionId+":"+problemId,"beforeAcSubmit");
    }

    @Override
    public List<RankResult> getRank(Integer competitionId, Integer start, Integer end) {
        List<RankResult> rankResults = new ArrayList<>();
        Set<Object> users=redisTemplate.opsForZSet().reverseRange("rank:"+competitionId,start,end);
        List<CompetitionProblemSet> competitionProblemSets= competitionProblemSetMapper.selectProblemsByCompetitionId(competitionId);
        int count=start;
        for(Object user:users){
            String userId=(String)user;
            RankResult rankResult = new RankResult();
            rankResult.setUserName((String)redisTemplate.opsForValue().get(userId));
            List<RankProblem> rankProblems=new ArrayList<>();
            forEachProblem(competitionId, userId, rankResult, rankProblems, competitionProblemSets);
            rankResult.setRank(++count);
            rankResults.add(rankResult);
        }
        return rankResults;
    }

    @Override
    public RankResult getRankResult(Integer competitionId, String userId) {
        RankResult rankResult = new RankResult();
        Long rank=(Long) redisTemplate.opsForZSet().reverseRank("rank:"+competitionId, userId);
        if(rank==null)return null;
        rankResult.setUserName((String)redisTemplate.opsForValue().get(userId));
        List<RankProblem> rankProblems=new ArrayList<>();
        List<CompetitionProblemSet> competitionProblemSets= competitionProblemSetMapper.selectProblemsByCompetitionId(competitionId);
        forEachProblem(competitionId, userId, rankResult, rankProblems, competitionProblemSets);
        rankResult.setRank(rank.intValue()+1);
        return rankResult;
    }

    private void forEachProblem(Integer competitionId, String userId, RankResult rankResult, List<RankProblem> rankProblems, List<CompetitionProblemSet> competitionProblemSets) {
        int []acSum={0};
        competitionProblemSets.forEach(p->{
                    Object val=redisTemplate.opsForValue().get("firstBloodPerson:"+competitionId+":"+p.getProblemId());
                    boolean valAc=(boolean)redisTemplate.opsForHash().get(userId+":"+competitionId+":"+p.getProblemId(),"isAc");
                    if(valAc)acSum[0]++;
                    Integer bAc=(Integer)redisTemplate.opsForHash().get(userId+":"+competitionId+":"+p.getProblemId(),"beforeAcSubmit");
                    Object accTime=redisTemplate.opsForHash().get(userId+":"+competitionId+":"+p.getProblemId(),"acTime");
                    LocalDateTime localDateTime=null;
                    if(accTime!=null){
                        localDateTime=LocalDateTime.parse((String)accTime);
                    }
                    if(val!=null&&val.toString().equals(userId))
                        rankProblems.add(
                                RankProblem.builder()
                                        .isAc(true)
                                        .isFirst(true)
                                        .acTime(localDateTime)
                                        .beforeAcSubmit(bAc)
                                        .build()
                        );
                    else if(valAc)
                        rankProblems.add(
                                RankProblem.builder()
                                        .isAc(true)
                                        .isFirst(false)
                                        .acTime(localDateTime)
                                        .beforeAcSubmit(bAc)
                                        .build()
                        );
                    else
                        rankProblems.add(
                                RankProblem.builder()
                                        .isAc(false)
                                        .isFirst(false)
                                        .acTime(localDateTime)
                                        .beforeAcSubmit(bAc)
                                        .build()
                        );
                }
        );
        rankResult.setAcSum(acSum[0]);
        rankResult.setRankProblems(rankProblems);
    }

    @Override
    public void addAProblem(Integer competitionId, Integer problemId) {
        List<User> users=userMapper.getAllUsers();
        users.forEach(user->{
            redisTemplate.opsForHash().put(user.getUserId()+":"+competitionId+":"+problemId,"isAc",false);
            redisTemplate.opsForHash().put(user.getUserId()+":"+competitionId+":"+problemId,"beforeAcSubmit",0);
        });
    }

    @Override
    public void delAProblem(Integer competitionId, Integer problemId) {
        List<User> users=userMapper.getAllUsers();
        redisTemplate.delete("firstBloodPerson:"+competitionId+":"+problemId);
        redisTemplate.delete("firstBloodTime:"+competitionId+":"+problemId);
        users.forEach(user->{
            redisTemplate.delete(user.getUserId()+":"+competitionId+":"+problemId);
        });
    }
}
