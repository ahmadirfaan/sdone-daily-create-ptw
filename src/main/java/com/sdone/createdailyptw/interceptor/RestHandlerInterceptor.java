package com.sdone.createdailyptw.interceptor;


import com.sdone.createdailyptw.service.LoggingService;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RestHandlerInterceptor implements HandlerInterceptor {

    private final LoggingService loggingService;

    @Autowired
    public RestHandlerInterceptor(LoggingService loggingService) {
        this.loggingService = loggingService;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(request.getMethod().equals(HttpMethod.GET.name())
                || request.getMethod().equals(HttpMethod.DELETE.name())
                || request.getMethod().equals(HttpMethod.PUT.name()))    {
            loggingService.displayReq(request,null);
        }
        return true;
    }
}
