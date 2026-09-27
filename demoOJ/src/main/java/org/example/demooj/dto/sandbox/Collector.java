package org.example.demooj.dto.sandbox;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Collector {
    private String name;
    private Long max;
}
