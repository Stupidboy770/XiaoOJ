package org.example.demooj.dto;

import lombok.Getter;

@Getter
public enum LanguageRun {

    CPP("cpp"),
    JAVA("java"),
    PYTHON("python");

    private final String language;

    LanguageRun(String language) {
        this.language = language;
    }
}
