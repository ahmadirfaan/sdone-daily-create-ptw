package com.sdone.createdailyptw.interceptor;

import com.sdone.createdailyptw.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
public class InterceptorConfiguration implements WebMvcConfigurer {

    @Autowired
    private LoggingService loggingService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RestHandlerInterceptor(loggingService));
    }
}
