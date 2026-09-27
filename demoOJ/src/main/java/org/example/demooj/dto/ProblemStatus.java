package org.example.demooj.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProblemStatus {

    ACCEPTED("Accepted"),
    WRONG_ANSWER("Wrong answer"),
    MEMORYLIMITEXCEEDED("Memory limit exceeded"),
    TIMELIMITEXCEEDED("Time limit exceeded"),
    COMPILEERROR("Compile error"),
    ERROR("Error"),
    RUNTIME_ERROR("Runtime error"),
    NONZEROEXITSTATUS("Nonzero exit status"),;

    private String status;

    ProblemStatus(String status) {
        this.status = status;
    }
    @JsonValue
    public String getStatus() {
        return status;
    }
}
