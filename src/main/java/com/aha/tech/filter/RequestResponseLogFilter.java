package com.aha.tech.filter;

import com.aha.tech.constant.OrderedConstant;
import com.aha.tech.filter.wrapper.RequestWrapper;
import com.aha.tech.filter.wrapper.ResponseWrapper;
import com.aha.tech.threadlocal.RequestLogThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * luweihong
 */
@Component
@Order(OrderedConstant.REQUEST_RESPONSE_FILTER_ORDERED)
@WebFilter(filterName = "RequestResponseLogFilter", urlPatterns = "/*")
public class RequestResponseLogFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLogFilter.class);

    private String IGNORE_URI = "actuator/prometheus";

    private String actuatorPrefix;

    private String swaggerPrefix;

    private String webjarsPrefix;

    private String docPrefix;

    @Override
    public void init(FilterConfig filterConfig) {
        ServletContext context = filterConfig.getServletContext();
        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(context);
        String contextPath = ac.getEnvironment().getProperty("common.server.tomcat.contextPath", "/");
        if (contextPath.equals("/")) {
            contextPath = Strings.EMPTY;
        }
        actuatorPrefix = contextPath + "/actuator/prometheus";
        swaggerPrefix = contextPath + "/swagger";
        webjarsPrefix = contextPath + "/webjars";
        docPrefix = contextPath + "/v2/api-docs";
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        String uri = StringUtils.isBlank(queryString) ? requestURL.toString() : requestURL.append("?").append(queryString).toString();

        if (skipLogging(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        RequestWrapper requestWrapper = new RequestWrapper(request);
        ResponseWrapper responseWrapper = new ResponseWrapper(response);

        try {
            StringBuilder requestLog = buildRequestLog(uri, requestWrapper);
            RequestLogThreadLocal.set(requestLog.toString());
            if (null == requestWrapper) {
                chain.doFilter(request, response);
            } else {
                chain.doFilter(requestWrapper, responseWrapper);
            }
            buildResponseLog(response, responseWrapper);
        } finally {
            RequestLogThreadLocal.remove();
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * 构造请求日志
     * @param uri
     * @param requestWrapper
     * @return
     */
    private StringBuilder buildRequestLog(String uri, RequestWrapper requestWrapper) {
        StringBuilder requestLog = new StringBuilder();

        requestLog.append(System.lineSeparator());
        requestLog.append("uri = ").append(uri);
        requestLog.append(System.lineSeparator());

        requestLog.append(System.lineSeparator());
        Enumeration<String> headers = requestWrapper.getHeaderNames();
        requestLog.append("header = ").append(System.lineSeparator());
        while (headers.hasMoreElements()) {
            String k = headers.nextElement();
            String v = requestWrapper.getHeader(k);
            requestLog.append(k).append(":").append(v).append(System.lineSeparator());
        }
        requestLog.append(System.lineSeparator());

        requestLog.append(System.lineSeparator());
        requestLog.append("body = ").append(requestWrapper.getBody());
        requestLog.append(System.lineSeparator());

        logger.info("{}", requestLog);
        return requestLog;
    }

    /**
     * 构造响应日志
     * @param response
     * @param responseWrapper
     * @throws IOException
     */
    private void buildResponseLog(HttpServletResponse response, ResponseWrapper responseWrapper) throws IOException {
        String result = new String(responseWrapper.getResponseData());
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(result.getBytes());
        outputStream.flush();
        outputStream.close();
        // 打印response
        StringBuilder responseLog = new StringBuilder(System.lineSeparator());
        responseLog.append("response status = ").append(responseWrapper.getStatus()).append(System.lineSeparator());
        responseLog.append("response body = ").append(System.lineSeparator());
        responseLog.append(result);

        logger.info("{}", responseLog);
    }

    /**
     * 是否跳过日志输出
     * @param URI
     * @return
     */
    private Boolean skipLogging(String URI) {
        return URI.equals(actuatorPrefix)
                || URI.startsWith(swaggerPrefix)
                || URI.startsWith(webjarsPrefix)
                || URI.startsWith(docPrefix);
    }
}



