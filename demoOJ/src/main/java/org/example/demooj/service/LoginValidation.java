package org.example.demooj.service;

public interface LoginValidation {

    String loginUser(String userId, String password);

    boolean isAdmin(String userId);

    Integer getDisAdmin();
}
