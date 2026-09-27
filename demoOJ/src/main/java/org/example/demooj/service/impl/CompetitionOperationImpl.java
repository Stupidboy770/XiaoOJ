package org.example.demooj.service.impl;

import jakarta.annotation.Resource;
import org.example.demooj.entity.CompetitionInformation;
import org.example.demooj.entity.CompetitionProblemSet;
import org.example.demooj.entity.Problem;
import org.example.demooj.mapper.CompetitionMapper;
import org.example.demooj.mapper.CompetitionProblemSetMapper;
import org.example.demooj.mapper.ProblemMapper;
import org.example.demooj.mapper.UserSubmittedLogMapper;
import org.example.demooj.service.CompetitionOperation;
import org.example.demooj.service.RankEnd;
import org.example.demooj.service.StatusAndRank;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
@Service
public class CompetitionOperationImpl implements CompetitionOperation {

    @Resource
    private CompetitionMapper competitionMapper;
    @Resource
    private CompetitionProblemSetMapper competitionProblemSetMapper;
    @Resource
    private UserSubmittedLogMapper userSubmittedLogMapper;
    @Resource
    private ProblemMapper problemMapper;
    @Resource
    private StatusAndRank  statusAndRank;
    @Resource
    private RankEnd  rankEnd;

    @Override
    public Integer deleteCompetitionById(Integer id) {
        userSubmittedLogMapper.deleteByCompetitionId(id);
        statusAndRank.clearDatabase(id);
        rankEnd.cancelRank(id);
        rankEnd.deleteRank(id);
        competitionProblemSetMapper.deleteProblemByGameId(id);
        return competitionMapper.deleteCompetitionById(id);
    }

    @Override
    public CompetitionInformation selectCompetitionById(Integer id) {
        return  competitionMapper.selectCompetitionById(id);
    }

    @Override
    public List<CompetitionInformation> selectAllCompetition() {
        return competitionMapper.selectAllCompetition();
    }

    @Override
    public Integer insertCompetition(CompetitionInformation competitionInformation) {
        Integer row = competitionMapper.insertCompetition(competitionInformation);
        statusAndRank.addUserToRank(competitionInformation.getId());
        rankEnd.startRank(Date.from(competitionInformation.getEndTime().atZone(ZoneId.systemDefault()).toInstant()),competitionInformation.getId());
        return row;
    }

    @Override
    public Integer updateCompetition(CompetitionInformation competitionInformation) {
        return  competitionMapper.updateCompetition(competitionInformation);
    }

    @Override
    public Integer deleteProblem(Integer problemId, Integer competitionId) {
        statusAndRank.delAProblem(competitionId,problemId);
        return competitionProblemSetMapper.deleteProblem(problemId, competitionId);
    }

    @Override
    public Integer insertProblem(Integer problemId, Integer competitionId) {
        Problem problem = problemMapper.selectProblemById(problemId);
        statusAndRank.addAProblem(competitionId,problemId);
        return competitionProblemSetMapper.insertProblem(
                CompetitionProblemSet.builder()
                        .competitionId(competitionId)
                        .problemId(problemId)
                        .problemName(problem.getProblemName())
                        .build()
        );
    }

    @Override
    public List<CompetitionProblemSet> selectProblemsByCompetitionId(Integer competitionId) {
        return competitionProblemSetMapper.selectProblemsByCompetitionId(competitionId);
    }

    @Override
    public Integer submitSumIncrement(Integer problemId, Integer competitionId) {
        return competitionProblemSetMapper.submitSumIncrement(problemId, competitionId);
    }

    @Override
    public Integer acceptSumIncrement(Integer problemId, Integer competitionId) {
        return competitionProblemSetMapper.acceptSumIncrement(problemId, competitionId);
    }
}
