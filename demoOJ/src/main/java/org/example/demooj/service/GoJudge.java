package org.example.demooj.service;

import org.example.demooj.dto.JudgeResult;
import org.example.demooj.dto.LanguageRun;
import org.example.demooj.dto.ProblemStatus;
import org.example.demooj.dto.UserSubmit;

public interface GoJudge {

    //收集参数并调取getResult
    JudgeResult judge(UserSubmit userSubmit);
    //返回结果
    JudgeResult getResult(long timeLimit, long memoryLimit, String context, int problemId, int submittedId, Integer competitionId);

    JudgeResult getResultPython(long timeLimit, long memoryLimit, String context, int problemId, int submittedId, Integer competitionId);

    JudgeResult getResultJava(long timeLimit, long memoryLimit, String context, int problemId, int submittedId, Integer competitionId);
}
