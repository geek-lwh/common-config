package com.aha.tech.aop;

import com.aha.tech.annotation.Authentication;
import com.aha.tech.exception.AuthenticationFailedException;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @Author: luweihong
 * @Date: 2019/6/10
 */
@Aspect
@Component
public class AuthenticationHandlerAop {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationHandlerAop.class);

    private static final String USER_FILED = "userId";

    private static final String GET_USER_ID_METHOD_NAME = "getUserId";

    private static final String USER_PARAM_FILED = "user_id";

    @Pointcut("execution(* com.aha.tech.controller.*.*(..))")
    public void verify() {
    }

    @Before("verify()")
    public void handler(JoinPoint joinPoint) {
        MethodSignature joinPointObject = (MethodSignature) joinPoint.getSignature();
        Method method = joinPointObject.getMethod();
        Authentication classAnnotation = AnnotationUtils.findAnnotation(method.getDeclaringClass(), Authentication.class);
        if (classAnnotation == null) {
            if (!method.isAnnotationPresent(Authentication.class)) {
                return;
            }
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String api = request.getRequestURI();
        HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
        switch (httpMethod) {
            case POST: case PUT: case PATCH:
                verifyBody(joinPoint, api);
                break;
            case GET: case DELETE:
                verifyParams(request, api);
                break;
            default:
                break;
        }
    }

    /**
     * 校验body数据
     * @param joinPoint
     * @param api
     */
    private void verifyBody(JoinPoint joinPoint, String api) {
        Object[] paramsArray = joinPoint.getArgs();
        if (paramsArray.length <= 0) {
            logger.error("校验userId出现异常");
            throw new AuthenticationFailedException(api);
        }

        Object bean = paramsArray[0];
        Long userId = userIdExist(bean, GET_USER_ID_METHOD_NAME);
        if (userId == null || userId <= 0l) {
            throw new AuthenticationFailedException(api);
        }
    }

    /**
     * 校验参数
     * @param request
     * @param api
     */
    private void verifyParams(HttpServletRequest request, String api) {
        String uId = request.getParameter(USER_PARAM_FILED);

        if (StringUtils.isBlank(uId)) {
            throw new AuthenticationFailedException(api);
        }

        if (Long.parseLong(uId) <= 0l) {
            throw new AuthenticationFailedException(api);
        }
    }

    /**
     * 判断用户是否存在
     * 用户id必须是Long类型
     * @param u
     * @param method
     * @return
     */
    private Long userIdExist(Object u, String method) {
        Long value = null;
        Method[] m = u.getClass().getMethods();
        for (int i = 0; i < m.length; i++) {
            if ((method).toLowerCase().equals(m[i].getName().toLowerCase())) {
                try {
                    value = (Long) m[i].invoke(u);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return value;
    }
}
