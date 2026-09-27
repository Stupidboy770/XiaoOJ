package org.example.demooj.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompetitionInformation {
    //比赛ID
    private Integer id;
    //比赛标题
    private String title;
    //开始时间
    private LocalDateTime startTime;
    //持续时间
    private LocalDateTime endTime;
    //比赛描述
    private String description;
}
