package com.aha.tech.aop;

import com.aha.tech.exception.GlobalException;
import com.aha.tech.exception.HowlersException;
import com.aha.tech.exception.SeriousException;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Author: luweihong
 * @Date: 2020/11/30
 * controller 全局异常捕获aop
 */
@RestControllerAdvice
public class ControllerExceptionAop {

    @Value("${jaeger.sampler.enable:true}")
    private Boolean enable;

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionAop.class);

    @ExceptionHandler(value = Exception.class)
    public ModelAndView errorHandler(Exception ex) {
        if (ex instanceof GlobalException) {
            GlobalException globalException = (GlobalException) ex;
            reportTraceError(globalException);
            loggingByLevel(globalException);
            return newResponse(globalException.getCode(), globalException.getMessage());
        } else {
            return newResponse(500, ex.getMessage());
        }
    }

    /**
     * 根据错误级别输出对应级别的日志
     * @param ex
     */
    private void loggingByLevel(GlobalException ex) {
        if (ex instanceof HowlersException) {
            logger.warn(ex.getMessage(), ex);
        } else if (ex instanceof SeriousException) {
            logger.error(ex.getMessage(), ex);
        } else {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 上报错误到trace
     * @param ex
     */
    private void reportTraceError(GlobalException ex) {
        // 开启trace 则上报当前活动的span是错误的
        if (enable) {
            Tracer tracer = GlobalTracer.get();
            Span span = tracer.activeSpan();
            Tags.ERROR.set(span, true);
            span.log(ex.getMessage());
        }
    }

    /**
     * 构建视图
     * @param code
     * @param message
     * @return
     */
    private ModelAndView newResponse(Integer code, String message) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("code", code);
        modelAndView.addObject("message", message);

        return modelAndView;
    }

}
