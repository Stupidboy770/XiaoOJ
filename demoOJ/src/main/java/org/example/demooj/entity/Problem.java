package org.example.demooj.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Problem {

    /*
    *
    * MarkDown
    * */

    //问题ID
    Integer problemId;
    //问题名字
    String problemName;
    //问题描述
    String problemDescription;
    //问题输入描述
    String problemInputDescription;
    //问题输出描述
    String problemOutputDescription;
    //问题输入样例
    String problemInput;
    //问题输出样例
    String problemOutput;

    //测评要求
    Long memoryLimit;
    Long timeLimit;
}
