package com.project.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.project.platform.interceptor.LoginInterceptor;

import jakarta.annotation.Resource;


@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    @NonNull
    LoginInterceptor loginInterceptor;
    
    @Value("${files.uploads.path:uploads/}")
    private String fileUploadPath;

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
                        "/file/**",
                        "/error"

                );
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射文件访问路径
        registry.addResourceHandler("/file/**")
                .addResourceLocations("file:" + fileUploadPath);
    }
}