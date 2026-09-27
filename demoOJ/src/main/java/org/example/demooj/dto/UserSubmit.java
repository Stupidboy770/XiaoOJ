package org.example.demooj.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserSubmit {
    Integer problemId;
    Integer competitionId;
    String userId;
    String context;
    LanguageRun languageRun;
    LocalDateTime submittedTime;
}
