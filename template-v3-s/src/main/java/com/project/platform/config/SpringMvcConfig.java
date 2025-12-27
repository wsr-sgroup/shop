package com.project.platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.project.platform.interceptor.LoginInterceptor;

import jakarta.annotation.Resource;


@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    @NonNull
    LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                //允许直接访问的接口
                .excludePathPatterns(
                        "/",
                        "/common/login",
                        "/common/register",
                        "/common/retrievePassword",
                        "/common/currentUser",  
                        "/file/**"
                );
    }
}