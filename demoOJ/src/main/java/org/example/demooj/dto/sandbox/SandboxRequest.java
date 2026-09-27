package org.example.demooj.dto.sandbox;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class SandboxRequest {
    private String requestId;
    private List<SandboxCmd> cmd;
    private List<?> pipeMapping;
}
