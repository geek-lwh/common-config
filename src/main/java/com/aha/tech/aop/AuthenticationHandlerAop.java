package com.aha.tech.aop;

import com.aha.tech.anotation.Authentication;
import com.aha.tech.exception.AuthenticationFailedException;
import com.aha.tech.util.BeanUtil;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author: luweihong
 * @Date: 2019/6/10
 */
@Aspect
@Component
public class AuthenticationHandlerAop {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationHandlerAop.class);

    private static final String USER_FILED = "userId";

    private static final String USER_PARAM_FILED = "user_id";

    @Pointcut("execution(* com.aha.tech.controller.*.*(..))")
    public void verify() {
    }

    @Before("verify()")
    public void handler(JoinPoint joinPoint) throws Exception {
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
        HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
        switch (httpMethod) {
            case POST:
            case PUT:
            case PATCH:
                verifyBody(joinPoint);
                break;
            case GET:
            case DELETE:
                verifyParams(request);
                break;
            default:
                break;
        }
    }

    /**
     * 校验body数据
     * @param joinPoint
     */
    private void verifyBody(JoinPoint joinPoint) {
        Object[] paramsArray = joinPoint.getArgs();
        Object param = paramsArray[0];
        List<Object> list = new ArrayList<>();
        if (param instanceof List) {
            list.addAll((Collection<?>) param);
        } else {
            list.add(param);
        }

        for (Object obj : list) {
            Map<String, Object> body = BeanUtil.convertObjToMap(obj);
            if (!body.containsKey(USER_FILED)) {
                throw new AuthenticationFailedException();
            }

            Long userId = body.get(USER_FILED) == null ? null : Long.parseLong(body.get(USER_FILED).toString());
            if (userId == null || userId <= 0l) {
                throw new AuthenticationFailedException();
            }
        }
    }

    /**
     * 校验参数
     * @param request
     */
    private void verifyParams(HttpServletRequest request) {
        String uId = request.getParameter(USER_PARAM_FILED);
        if (StringUtils.isBlank(uId)) {
            throw new AuthenticationFailedException();
        }

        if (Long.parseLong(uId) <= 0l) {
            throw new AuthenticationFailedException();
        }
    }

}
