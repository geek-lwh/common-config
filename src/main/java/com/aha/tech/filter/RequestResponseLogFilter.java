package com.aha.tech.filter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.UUID;

/**
 * luweihong
 */
@Order(0)
@Component
@WebFilter(filterName = "RequestResponseLogFilter", urlPatterns = "/*")
public class RequestResponseLogFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLogFilter.class);


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        RequestWrapper requestWrapper = null;
        if (request instanceof HttpServletRequest) {
            requestWrapper = new RequestWrapper(request);
        }

        StringBuilder requestLog = new StringBuilder(System.lineSeparator());
        String uri = requestWrapper.getRequestURI();
        String requestId = StringUtils.isBlank(requestWrapper.getHeader("requestId")) ? UUID.randomUUID().toString() : requestWrapper.getHeader("requestId");
        
        Enumeration<String> headers = requestWrapper.getHeaderNames();

        requestLog.append("logId : ").append(requestId).append(System.lineSeparator());
        requestLog.append("uri : ").append(uri).append(System.lineSeparator());
        requestLog.append("header : ").append(System.lineSeparator());
        while (headers.hasMoreElements()) {
            String k = headers.nextElement();
            String v = requestWrapper.getHeader(k);
            requestLog.append(k).append("=").append(v).append(System.lineSeparator());
        }

        requestLog.append("body : ").append(System.lineSeparator());

        if (requestWrapper != null) {
            BufferedReader bufferedReader = requestWrapper.getReader();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                requestLog.append(line);
            }
        }

        logger.info("{}", requestLog);

        ResponseWrapper responseWrapper = new ResponseWrapper(response);

        if (null == requestWrapper) {
            filterChain.doFilter(request, response);
        } else {
            filterChain.doFilter(requestWrapper, responseWrapper);
        }

        String result = new String(responseWrapper.getResponseData());
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(result.getBytes());
        outputStream.flush();
        outputStream.close();
        // 打印response
        StringBuilder responseLog = new StringBuilder(System.lineSeparator());
        responseLog.append("logId : ").append(requestId).append(System.lineSeparator());
        responseLog.append("response status : ").append(responseWrapper.getStatus()).append(System.lineSeparator());
        responseLog.append("response body : ").append(System.lineSeparator());
        responseLog.append(result);

        logger.info("{}", responseLog);
    }

}



