package com.aha.tech.filter;

import com.aha.tech.constant.HeaderConstant;
import com.aha.tech.constant.OrderedConstant;
import com.aha.tech.util.MDCUtil;
import com.aha.tech.util.TracerUtils;
import com.google.common.collect.Maps;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.propagation.TextMapInjectAdapter;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;

/**
 * @Author: luweihong
 * @Date: 2020/11/4
 */
@Component
@Order(OrderedConstant.TRACE_FILTER_ORDERED)
@WebFilter(filterName = "tracerServletFilter", urlPatterns = "/*")
public class TracerServletFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(TracerServletFilter.class);


    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        Map<String, String> hMap = Maps.newHashMap();
        Tracer tracer = GlobalTracer.get();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String k = headerNames.nextElement();
            String v = request.getHeader(k);
            hMap.put(k, v);
        }

        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(request.getRequestURI());
        try {
            SpanContext parentSpan = tracer.extract(Format.Builtin.HTTP_HEADERS, new TextMapAdapter(hMap));
            if (parentSpan != null) {
                spanBuilder.asChildOf(parentSpan);
            }
        } catch (IllegalArgumentException e) {
            spanBuilder.withTag("Error", "extract from request fail, error msg:" + e.getMessage());
        }

        // set current activate span
        Span span = spanBuilder.start();
        MDCUtil.putTraceId(span.context().toTraceId());

        try (Scope scope = tracer.scopeManager().activate(span)) {
            Tags.HTTP_URL.set(span, request.getRequestURI());
            Tags.HTTP_METHOD.set(span, request.getMethod());
            span.setTag(TracerUtils.REQUEST_FROM, request.getHeader(HeaderConstant.REQUEST_SERVER_NAME));
            span.setTag(TracerUtils.REQUEST_IP, request.getHeader(HeaderConstant.REQUEST_SERVER_ADDRESS));
            chain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            Map err = TracerUtils.errorTraceMap(e);
            Tags.ERROR.set(span, true);
            span.log(err);
            logger.error(e.getMessage(), e);
        } finally {
            span.finish();
        }

    }

    @Override
    public void destroy() {

    }

    private void attachTraceInfo(Tracer tracer, Span span, final HttpServletRequest servletRequest) {
        // 你想要发送的k,v 格式
        Map<String, String> hMap = Maps.newHashMap();
        Enumeration<String> headerNames = servletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String k = headerNames.nextElement();
            String v = servletRequest.getHeader(k);
            hMap.put(k, v);
        }

        Tags.SPAN_KIND.set(tracer.activeSpan(), Tags.SPAN_KIND_CLIENT);
        Tags.HTTP_METHOD.set(tracer.activeSpan(), servletRequest.getMethod());
        Tags.HTTP_URL.set(tracer.activeSpan(), servletRequest.getRequestURI());
        // activeSpan().context() 有root,parent,child 等信息
        tracer.inject(tracer.activeSpan().context(), Format.Builtin.TEXT_MAP_INJECT, new TextMapInjectAdapter(hMap));
    }

}
