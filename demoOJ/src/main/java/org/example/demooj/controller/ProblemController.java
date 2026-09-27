package org.example.demooj.controller;

import jakarta.annotation.Resource;
import org.example.demooj.entity.Problem;
import org.example.demooj.service.ProblemOperation;
import org.example.demooj.service.impl.ProblemOperationImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProblemController {
    @Resource
    ProblemOperation problemOperationImpl;
    @GetMapping("/getAProblem")
    public ResponseEntity<Problem> getAProblem(@RequestParam String userId,@RequestParam Integer problemId){
        return new ResponseEntity<>(
                problemOperationImpl.selectProblemById(problemId), HttpStatus.OK
        );
    }
}
