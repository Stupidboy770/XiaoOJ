package org.example.demooj.mapper;

import org.example.demooj.entity.Problem;

import java.util.List;

public interface ProblemMapper {

    Integer addProblem(Problem problem);

    Integer deleteProblemById(Integer id);

    Integer updateProblemById(Problem problem);

    Problem selectProblemById(Integer id);

    List<Problem> selectAllProblem();

    Integer getMaxId();

}
