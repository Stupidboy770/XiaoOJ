package org.example.demooj.controller.admin;

import jakarta.annotation.Resource;
import org.example.demooj.entity.CompetitionInformation;
import org.example.demooj.entity.CompetitionProblemSet;
import org.example.demooj.service.CompetitionOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CompetitionUpdateController {

    @Resource
    private CompetitionOperation  competitionOperation;
    //获取所有比赛
    @GetMapping("/getAllCompetition")
    public ResponseEntity<List<CompetitionInformation>> getAllCompetition() {
        return new ResponseEntity<>(competitionOperation.selectAllCompetition(), HttpStatus.OK);
    }
    //修改单个比赛的信息
    @PostMapping("/changeCompetition")
    public ResponseEntity<Integer> changeCompetition(@RequestBody CompetitionInformation competitionInformation) {
        return new ResponseEntity<>(competitionOperation.updateCompetition(competitionInformation), HttpStatus.OK);
    }
    //获取单个比赛的题目集
    @GetMapping("/getGameProblem")
    public ResponseEntity<List<CompetitionProblemSet>> getGameProblem(Integer competitionId) {
        return new ResponseEntity<>(competitionOperation.selectProblemsByCompetitionId(competitionId), HttpStatus.OK);
    }
    //删除单个比赛
    @DeleteMapping("/deleteCompetition")
    public ResponseEntity<Integer> deleteCompetition(Integer competitionId) {
        return new  ResponseEntity<>(competitionOperation.deleteCompetitionById(competitionId), HttpStatus.OK);
    }
    //添加比赛
    @PostMapping("/addCompetition")
    public ResponseEntity<Integer> addCompetition(@RequestBody CompetitionInformation competitionInformation) {
        return new ResponseEntity<>(competitionOperation.insertCompetition(competitionInformation),HttpStatus.OK);
    }
    //添加问题
    @PostMapping("/addToProblem")
    public ResponseEntity<Integer> addProblem(Integer competitionId, Integer problemId) {
        return new ResponseEntity<>(competitionOperation.insertProblem(problemId,competitionId),HttpStatus.OK);
    }
    //删除问题
    @DeleteMapping("/deleteToProblem")
    public ResponseEntity<Integer> deleteProblem(Integer competitionId, Integer problemId) {
        return new ResponseEntity<>(competitionOperation.deleteProblem(problemId,competitionId),HttpStatus.OK);
    }
}
