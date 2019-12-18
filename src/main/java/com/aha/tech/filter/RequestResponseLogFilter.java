package com.aha.tech.filter;

import com.aha.tech.filter.wrapper.RequestWrapper;
import com.aha.tech.filter.wrapper.ResponseWrapper;
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
import java.io.IOException;
import java.util.Enumeration;

/**
 * luweihong
 */
@Order(1)
@Component
@WebFilter(filterName = "RequestResponseLogFilter", urlPatterns = "/*")
public class RequestResponseLogFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLogFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        String uri = StringUtils.isBlank(queryString) ? requestURL.toString() : requestURL.append("?").append(queryString).toString();

        if (skipLogging(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        RequestWrapper requestWrapper = new RequestWrapper(request);
        ResponseWrapper responseWrapper = new ResponseWrapper(response);

        StringBuilder requestLog = new StringBuilder(System.lineSeparator());
        Enumeration<String> headers = requestWrapper.getHeaderNames();

        requestLog.append("uri : ").append(uri).append(System.lineSeparator());
        requestLog.append("header : ").append(System.lineSeparator());
        while (headers.hasMoreElements()) {
            String k = headers.nextElement();
            String v = requestWrapper.getHeader(k);
            requestLog.append(k).append("=").append(v).append(System.lineSeparator());
        }

        requestLog.append("body : ").append(System.lineSeparator());

        requestLog.append(requestWrapper.getBody());
//        if (requestWrapper != null) {
//            BufferedReader bufferedReader = requestWrapper.getReader();
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                requestLog.append(requestWrapper.);
//            }
//        }

        logger.info("{}", requestLog);

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
        responseLog.append("response status : ").append(responseWrapper.getStatus()).append(System.lineSeparator());
        responseLog.append("response body : ").append(System.lineSeparator());
        responseLog.append(result);

        logger.info("{}", responseLog);
    }

    /**
     * 是否跳过日志输出
     * @param uri
     * @return
     */
    private Boolean skipLogging(String uri) {
        return uri.contains("prometheus") || uri.contains("webjars") || uri.contains("swagger") || uri.contains("api-docs") || uri.contains("favicon");
    }

}



