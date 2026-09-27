package org.example.demooj.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    Integer id;
    String userId;
    String name;
    String password;
    boolean admin;
}
