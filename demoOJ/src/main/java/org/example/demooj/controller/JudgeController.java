package org.example.demooj.controller;

import jakarta.annotation.Resource;
import org.example.demooj.dto.JudgeResult;
import org.example.demooj.dto.UserSubmit;
import org.example.demooj.service.GoJudge;
import org.example.demooj.service.impl.GoJudgeImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JudgeController {
    @Resource
    GoJudge goJudgeImpl;

    @PostMapping("/gojudge")
    public ResponseEntity<JudgeResult> judge(@RequestBody UserSubmit userSubmit) {
        return new ResponseEntity<>(goJudgeImpl.judge(userSubmit), HttpStatus.OK);
    }
}
