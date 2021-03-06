package com.aha.tech.interceptor;

import com.aha.tech.model.XEnvDto;
import com.aha.tech.threadlocal.XEnvThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: luweihong
 * @Date: 2019/11/28
 */
public class EnvInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(EnvInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        XEnvDto xEnvDto = new XEnvDto(request);
        XEnvThreadLocal.set(xEnvDto);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        XEnvThreadLocal.remove();
    }

}
