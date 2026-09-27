package org.example.demooj.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.demooj.entity.CompetitionInformation;
import org.example.demooj.entity.CompetitionProblemSet;
import org.example.demooj.mapper.CompetitionMapper;
import org.example.demooj.service.CompetitionOperation;
import org.example.demooj.service.LoginValidation;
import org.example.demooj.service.impl.CompetitionOperationImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class CompetitionController {

    @Resource
    CompetitionOperation competitionOperationImpl;
    @Resource
    LoginValidation loginValidation;

    @GetMapping("/getGameList")
    public ResponseEntity<List<CompetitionInformation>> getAllCompetitionInformation() {
        return new ResponseEntity<>(
                competitionOperationImpl.selectAllCompetition(),
                HttpStatus.OK
        );
    }

    @GetMapping("/getSingle")
    public ResponseEntity<CompetitionInformation> getUniqueCompetition(@RequestParam Integer id, HttpServletRequest request){
        CompetitionInformation competitionInformation = competitionOperationImpl.selectCompetitionById(id);
        if(competitionInformation==null)return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        boolean isAdmin= loginValidation.isAdmin((String) request.getAttribute("userId"));
        if(competitionInformation.getStartTime().isAfter(LocalDateTime.now())&&!isAdmin){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(
                competitionOperationImpl.selectCompetitionById(id)
                , HttpStatus.OK
        );
    }

    @GetMapping("/getProblemSet")
    public ResponseEntity<List<CompetitionProblemSet>> getAllProblemSet(@RequestParam Integer id,HttpServletRequest request){
        CompetitionInformation competitionInformation = competitionOperationImpl.selectCompetitionById(id);
        if(competitionInformation==null)return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        boolean isAdmin= loginValidation.isAdmin((String) request.getAttribute("userId"));
        if(competitionInformation.getStartTime().isAfter(LocalDateTime.now())&&!isAdmin){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(
                competitionOperationImpl.selectProblemsByCompetitionId(id)
                , HttpStatus.OK
        );
    }

    @GetMapping("/getUser")
    public ResponseEntity<Integer> getUser(){
        return new ResponseEntity<>(
                loginValidation.getDisAdmin(),HttpStatus.OK
        );
    }
}
