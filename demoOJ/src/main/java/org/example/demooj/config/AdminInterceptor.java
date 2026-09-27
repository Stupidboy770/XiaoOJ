package org.example.demooj.config;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demooj.service.LoginValidation;
import org.example.demooj.service.impl.LoginValidationImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Resource
    LoginValidation loginValidationImpl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        boolean isAdmin = loginValidationImpl.isAdmin(userId);
        if (isAdmin) return isAdmin;
        response.setStatus(401);
        return false;
    }
}
