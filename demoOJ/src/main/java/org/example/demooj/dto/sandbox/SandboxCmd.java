package org.example.demooj.dto.sandbox;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class SandboxCmd {
    private List<String> args;
    private List<String> env;
    // 元素：MemoryFileDTO / CollectorDTO / null
    private List<Object> files;
    private Long cpuLimit;
    private Long clockLimit;
    private Long memoryLimit;
    private Integer procLimit;
    private Double cpuRate;
    // copyIn value: MemoryFileDTO / PreparedFileDTO
    private Map<String, Object> copyIn;
    private List<String> copyOut;
    private List<String> copyOutCached;
}
