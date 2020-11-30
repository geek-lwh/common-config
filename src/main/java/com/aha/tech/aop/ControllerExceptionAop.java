package com.aha.tech.aop;

import com.aha.tech.exception.GlobalException;
import com.aha.tech.exception.HowlersException;
import com.aha.tech.exception.SeriousException;
import com.aha.tech.threadlocal.RequestLogThreadLocal;
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

    /**
     * 抛出warn级别异常
     * @param ex
     * @return
     */
    @ExceptionHandler(value = HowlersException.class)
    public ModelAndView warnHandler(HowlersException ex) {
        reportWarnEvent(ex);
        return newResponse(ex);
    }

    /**
     * 抛出error级别异常
     * @param ex
     * @return
     */
    @ExceptionHandler(value = SeriousException.class)
    public ModelAndView errorHandler(SeriousException ex) {
        reportErrorEvent(ex);
        return newResponse(ex);
    }

    // todo 集成component advice controller

    /**
     * 上报警告级别日志
     * @param ex
     */
    private void reportWarnEvent(GlobalException ex) {
        logger.warn(ex.message(), ex);
        logger.warn(getRequestChainInfo());
        // 开启trace 则上报当前活动的span是错误的
        reportTrace(ex);
    }

    /**
     * 上报错误级别日志
     * @param ex
     */
    private void reportErrorEvent(GlobalException ex) {
        logger.error(ex.message(), ex);
        logger.error(getRequestChainInfo());
        // 开启trace 则上报当前活动的span是错误的
        reportTrace(ex);
    }

    /**
     * 上报trace
     * @param ex
     */
    private void reportTrace(GlobalException ex) {
        if (enable) {
            Tracer tracer = GlobalTracer.get();
            Span span = tracer.activeSpan();
            Tags.ERROR.set(span, true);
            span.log(ex.message());
        }
    }

    /**
     * 构建视图
     * @param globalException
     * @return
     */
    private ModelAndView newResponse(GlobalException globalException) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("code", globalException.code());
        modelAndView.addObject("message", globalException.message());

        return modelAndView;
    }

    /**
     *
     * 获取request信息
     * @return
     */
    private String getRequestChainInfo() {
        return RequestLogThreadLocal.get();
    }

}
