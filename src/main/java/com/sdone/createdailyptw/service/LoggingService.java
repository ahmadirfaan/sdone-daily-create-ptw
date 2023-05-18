package com.sdone.createdailyptw.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class LoggingService {

    private static final Logger loggerRest = LoggerFactory.getLogger("REST-SERVICE");

    public void displayReq(HttpServletRequest request, Object body) {
        var reqMessage = new StringBuilder();
        var parameters = getParameters(request);

        reqMessage.append("REQUEST ");
        reqMessage.append("method = [").append(request.getMethod()).append("]");
        reqMessage.append(" path = [").append(request.getRequestURI()).append("] ");

        if (!parameters.isEmpty()) {
            reqMessage.append(" parameters = [").append(parameters).append("] ");
        }

        if (!Objects.isNull(body)) {
            reqMessage.append(" body = [").append(body).append("]");
        }

        loggerRest.info("log Request: {}", reqMessage);
    }

    public void displayResp(HttpServletRequest request, HttpServletResponse response, Object body) {
        var respMessage = new StringBuilder();
        var headers = getHeaders(response);
        respMessage.append("RESPONSE ");
        respMessage.append(" method = [").append(request.getMethod()).append("]");
        if (!headers.isEmpty()) {
            respMessage.append(" ResponseHeaders = [").append(headers).append("]");
        }
        respMessage.append(" responseBody = [").append(body).append("]");

        loggerRest.info("logResponse: {}", respMessage);
    }

    private Map<String, String> getHeaders(HttpServletResponse response) {
        var headers = new HashMap<String, String>();
        var headerMap = response.getHeaderNames();
        for (String str : headerMap) {
            headers.put(str, response.getHeader(str));
        }
        return headers;
    }

    private Map<String, String> getParameters(HttpServletRequest request) {
        var parameters = new HashMap<String, String>();
        var params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            String paramValue = request.getParameter(paramName);
            parameters.put(paramName, paramValue);
        }
        return parameters;
    }
}
