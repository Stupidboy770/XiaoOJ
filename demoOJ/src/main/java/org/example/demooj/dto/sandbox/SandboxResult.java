package org.example.demooj.dto.sandbox;

import lombok.Data;

import java.util.Map;

@Data
public class SandboxResult {
    private String status;
    private String error;
    private Integer exitStatus;

    private Long time;      // cpu时间，纳秒
    private Long memory;    // 内存 byte
    private Long runTime;   // 墙上现实时间，纳秒

    private Map<String, String> files;
    private Map<String, String> fileIds;
}
