package org.example.demooj.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    JwtInterceptor jwtInterceptor;
    @Resource
    AdminInterceptor adminInterceptor;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 包下所有Controller，自动拼接 /admin 前缀
        configurer.addPathPrefix("/admin",
                c -> c.getPackage().getName().startsWith("org.example.demooj.controller.admin"));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login")
                .order(1);
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**")
                .order(2);
    }
}
