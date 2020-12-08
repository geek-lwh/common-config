package com.aha.tech.filter;

import com.aha.tech.constant.OrderedConstant;
import com.aha.tech.threadlocal.TraceThreadLocal;
import com.aha.tech.util.MDCUtil;
import com.aha.tech.util.TraceUtil;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.util.GlobalTracer;
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
import java.util.Map;

/**
 * @Author: luweihong
 * @Date: 2020/11/4
 */
@Component
@Order(OrderedConstant.TRACE_FILTER_ORDERED)
@WebFilter(filterName = "tracerServletFilter", urlPatterns = "/*")
public class TraceFilter implements Filter {

    private String actuatorPrefix = "/actuator/prometheus";

    private final Logger logger = LoggerFactory.getLogger(TraceFilter.class);

    private static String IGNORE_TRACE_API;


    @Override
    public void init(FilterConfig filterConfig) {
        ServletContext context = filterConfig.getServletContext();
        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(context);
        String contextPath = ac.getEnvironment().getProperty("common.server.tomcat.contextPath", "/");
        if (contextPath.equals("/")) {
            contextPath = Strings.EMPTY;
        }
        IGNORE_TRACE_API = contextPath + actuatorPrefix;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        Boolean isExclude = isExcludeURI(request.getRequestURI());
        doTracingFilter(servletRequest, servletResponse, chain, request);
    }

    /**
     * 记录tracing
     * @param servletRequest
     * @param servletResponse
     * @param chain
     * @param request
     */
    private void doTracingFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain, HttpServletRequest request) {
        String uri = request.getRequestURI();
        Tracer tracer = GlobalTracer.get();
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(request.getRequestURI());
        try {
            Map<String, String> hMap = TraceUtil.parseTraceContext(request);
            SpanContext parentSpan = tracer.extract(Format.Builtin.HTTP_HEADERS, new TextMapAdapter(hMap));
            if (parentSpan != null) {
                spanBuilder.asChildOf(parentSpan);
            }
        } catch (IllegalArgumentException e) {
            spanBuilder.withTag("Error", "extract from request fail, error msg:" + e.getMessage());
            logger.error("解析trace context出现异常", e);
        }

        Span span = spanBuilder.start();
        MDCUtil.putTraceId(span.context().toTraceId());

        try (Scope scope = tracer.scopeManager().activate(span)) {
            TraceUtil.setServerTags(request, uri, IGNORE_TRACE_API, span);
            chain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            TraceUtil.setCapturedErrorsTags(e);
        } finally {
            span.finish();
            TraceThreadLocal.remove();
        }
    }


    @Override
    public void destroy() {

    }

}
