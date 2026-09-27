package org.example.demooj.service;

import org.example.demooj.entity.Problem;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProblemOperation {
    //查找全部题目
    List<Problem> selectAllProblems();
    //修改题目 带ID
    Integer updateProblem(Problem problem);
    //根据ID删除题目
    Integer deleteProblemById(Integer id);
    //不传入ID
    Integer addProblem(Problem problem);
    //根据ID查找某一个题目
    Problem selectProblemById(Integer id);
    //指定题目ID与zip并解压
    Integer unzipProblem(MultipartFile files,Integer problemId);
}
