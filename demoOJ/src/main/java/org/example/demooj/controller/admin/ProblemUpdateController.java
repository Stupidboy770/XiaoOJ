package org.example.demooj.controller.admin;

import jakarta.annotation.Resource;
import org.example.demooj.entity.Problem;
import org.example.demooj.service.ProblemOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ProblemUpdateController {

    @Resource
    ProblemOperation problemOperation;

    //获取所有问题
    @GetMapping("/getAllProblem")
    public ResponseEntity<List<Problem>> getAllProblem(){
        return new ResponseEntity<>(
                problemOperation.selectAllProblems(),
                HttpStatus.OK
        );
    }
    //修改单个问题的描述
    @PostMapping("/updateProblem")
    public ResponseEntity<Integer> updateProblem(@RequestBody Problem problem){
        return new ResponseEntity<>(
                problemOperation.updateProblem(problem),
                HttpStatus.OK
        );
    }
    //添加问题
    @PostMapping("/addProblem")
    public ResponseEntity<Integer> addProblem(@RequestBody Problem problem){
        return new ResponseEntity<>(
                problemOperation.addProblem(problem),
                HttpStatus.OK
        );
    }
    //根据ID删除问题
    @DeleteMapping("/deleteProblem")
    public ResponseEntity<Integer> deleteProblem(Integer id){
        return new ResponseEntity<>(
                problemOperation.deleteProblemById(id),
                HttpStatus.OK
        );
    }
    //根据问题ID传递zip数据包
    @PostMapping("/postExample")
    public ResponseEntity<Integer> postExample(@RequestPart MultipartFile files,@RequestParam Integer problemId){
        return new ResponseEntity<>(
                problemOperation.unzipProblem(files,problemId),
                HttpStatus.OK
        );
    }
}
