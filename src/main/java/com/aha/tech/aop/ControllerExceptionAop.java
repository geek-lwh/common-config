package com.aha.tech.aop;

import com.aha.tech.exception.AuthenticationFailedException;
import com.aha.tech.model.RpcResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.ServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * 接收controller层未捕获的异常
 */
@ControllerAdvice
public class ControllerExceptionAop {

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionAop.class);

    @Value("${application.code}")
    private String applicationCode;

    private final String unknownExceptionCode = "4200";

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public RpcResponse handle(Exception ex, ServletRequest request) {
        logger.error(ex.getMessage(), ex);
        StringBuilder sb = new StringBuilder();
        sb.append("错误信息 : ").append(ex).append(System.lineSeparator());
        if (request != null && request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            String uri = wrapper.getRequestURI();
            Enumeration<String> headers = wrapper.getHeaderNames();
            sb.append("请求行 : ").append(uri).append(System.lineSeparator());
            sb.append("请求头 : ").append(System.lineSeparator());
            while (headers.hasMoreElements()) {
                String k = headers.nextElement();
                String v = wrapper.getHeader(k);
                sb.append(k).append("=").append(v).append(System.lineSeparator());
            }

            String body = StringUtils.toEncodedString(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            sb.append("请求体 : ").append(body);
        }

        logger.error("{}", sb);
        if (ex instanceof AuthenticationFailedException) {
            return new RpcResponse(((AuthenticationFailedException) ex).getCode(), ex.getMessage());
        }

        Integer code = Integer.parseInt(applicationCode + unknownExceptionCode);
        return new RpcResponse(code, ex.getMessage());
    }

}
