package org.example.demooj;

import jakarta.annotation.Resource;
import org.example.demooj.entity.CompetitionProblemSet;
import org.example.demooj.entity.User;
import org.example.demooj.mapper.*;
import org.example.demooj.service.RankEnd;
import org.example.demooj.service.StatusAndRank;
import org.example.demooj.service.impl.CompetitionOperationImpl;
import org.example.demooj.service.impl.ProblemOperationImpl;
import org.example.demooj.service.impl.GoJudgeImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@SpringBootTest
class DemoOjApplicationTests {

    @Resource
    GoJudgeImpl goJudge;
    @Resource
    ProblemOperationImpl problemOperation;
    @Resource
    ProblemMapper problemMapper;
    @Resource
    CompetitionOperationImpl competitionOperation;
    @Resource
    UserSubmittedLogMapper userSubmittedLogMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private StatusAndRank  statusAndRank;
    @Resource
    private RankEnd rankEnd;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CompetitionProblemSetMapper competitionProblemSetMapper;

    @Test
    void registryNow(){
        //Mention!!!!!!!!!!!!!!!!!!!!!!
        int competitionId = 55;

        User user =User.builder()
                //123456 vYUllGAxl2d14BXgSqW+2PSTuLdhZmRi
                .password("")
                .userId("")
                .name("")
                .build();

        userMapper.addUser(user);

        List<CompetitionProblemSet> competitionProblemSets= competitionProblemSetMapper.selectProblemsByCompetitionId(competitionId);

        redisTemplate.opsForZSet().add("rank:"+competitionId,user.getUserId(),0);
        redisTemplate.opsForValue().set(user.getUserId(),user.getName());
        competitionProblemSets.forEach(p->{
            redisTemplate.opsForHash().put(user.getUserId()+":"+competitionId+":"+p.getProblemId(),"isAc",false);
            redisTemplate.opsForHash().put(user.getUserId()+":"+competitionId+":"+p.getProblemId(),"beforeAcSubmit",0);
        });
    }


}
