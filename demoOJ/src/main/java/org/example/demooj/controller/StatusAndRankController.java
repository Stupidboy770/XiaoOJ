package org.example.demooj.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.demooj.entity.RankResult;
import org.example.demooj.entity.User;
import org.example.demooj.entity.UserSubmittedLog;
import org.example.demooj.service.CompetitionOperation;
import org.example.demooj.service.LoginValidation;
import org.example.demooj.service.RankEnd;
import org.example.demooj.service.StatusAndRank;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
public class StatusAndRankController {

    @Resource
    private LoginValidation loginValidation;
    @Resource
    private StatusAndRank statusAndRank;
    @Resource
    private CompetitionOperation competitionOperation;
    @Resource
    private RankEnd rankEnd;

    @GetMapping("/getUserStatus")
    public ResponseEntity<List<UserSubmittedLog>> getUserStatus(HttpServletRequest request, @RequestParam("competitionId") Integer competitionId
    ,Integer problemId,String selectUserId,String language,Integer offset,Integer limit) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("count",statusAndRank.selectOneUserSubmittedCount((String) request.getAttribute("userId"),selectUserId,competitionId,problemId,language).toString());
        return new ResponseEntity<>(
                statusAndRank.selectOneUserSubmittedLog((String) request.getAttribute("userId"),selectUserId,competitionId,problemId,language,offset,limit)
                ,headers
                , HttpStatus.OK);
    }

    @GetMapping("/getSubmitCount")
    public ResponseEntity<Integer> getSubmitCount(HttpServletRequest request,Integer competitionId) {
        return new ResponseEntity<>(
                statusAndRank.getSubmitCount(competitionId,(String) request.getAttribute("userId"))
                ,HttpStatus.OK
        );
    }

    @GetMapping("/getRank")
    public ResponseEntity<List<RankResult>> getRank(HttpServletRequest request,@RequestParam("competitionId") Integer competitionId, @RequestParam("startIndex") Integer start, @RequestParam("endIndex") Integer end) {
        List<RankResult> rankResultList;
        LocalDateTime endTime=competitionOperation.selectCompetitionById(competitionId).getEndTime();
        String userId=(String)request.getAttribute("userId");
        //约束左闭右开
        if(LocalDateTime.now().isAfter(endTime)){
            rankResultList=rankEnd.getRank(competitionId,start,end);
            if(!loginValidation.isAdmin(userId)){
                rankResultList.add(rankEnd.getRankResult(competitionId,userId));
            }else rankResultList.add(null);
        }else{
            rankResultList=statusAndRank.getRank(competitionId,start,end-1);
            if(!loginValidation.isAdmin(userId)){
                rankResultList.add(statusAndRank.getRankResult(competitionId,userId));
            }else rankResultList.add(null);
        }
        return new ResponseEntity<>(
                rankResultList,
                HttpStatus.OK
        );
    }
}
