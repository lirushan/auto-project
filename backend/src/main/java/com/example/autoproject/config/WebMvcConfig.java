package com.example.autoproject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final StudentAuthInterceptor studentAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(studentAuthInterceptor)
                .addPathPatterns("/api/v1/student/**")
                .excludePathPatterns("/api/v1/student/send-code", "/api/v1/student/login");
    }
}
