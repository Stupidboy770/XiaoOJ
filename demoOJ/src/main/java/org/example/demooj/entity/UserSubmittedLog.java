package org.example.demooj.entity;

import lombok.Builder;
import lombok.Data;
import org.example.demooj.dto.LanguageRun;

import java.time.LocalDateTime;

@Data
@Builder
public class UserSubmittedLog {

    //提交ID
    private Integer id;
    //用户名
    private String userId;
    //问题ID
    private Integer problemId;
    //提交时间
    private LocalDateTime submittedTime;
    //判题结果
    private String result;
    //所用内存
    private Long memory;
    //所用时间
    private Long runTime;
    //提交代码
    private String code;
    //比赛ID
    private Integer competitionId;
    //所用语言
    private String language;
}
