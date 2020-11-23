package com.aha.tech.filter;

import com.aha.tech.constant.HeaderConstant;
import com.aha.tech.constant.OrderedConstant;
import com.aha.tech.util.MDCUtil;
import com.aha.tech.util.TracerUtils;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @Author: luweihong
 * @Date: 2020/11/4
 */
@Component
@Order(OrderedConstant.TRACE_FILTER_ORDERED)
@WebFilter(filterName = "tracerServletFilter", urlPatterns = "/*")
public class TraceFilter implements Filter {

    private String actuatorPrefix;

    private String swaggerPrefix;

    private String webjarsPrefix;

    private String docPrefix;


    private final Logger logger = LoggerFactory.getLogger(TraceFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
        ServletContext context = filterConfig.getServletContext();
        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(context);
        String contextPath = ac.getEnvironment().getProperty("common.server.tomcat.contextPath", "/");
        actuatorPrefix = contextPath + "/actuator";
        swaggerPrefix = contextPath + "/swagger";
        webjarsPrefix = contextPath + "/webjars";
        docPrefix = contextPath + "/v2/api-docs";

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Boolean isExclude = isExcludeURI(request.getRequestURI());
        try {
            if (isExclude) {
                chain.doFilter(servletRequest, servletResponse);
            } else {
                doTracingFiltere(servletRequest, servletResponse, chain, request);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    /**
     * 记录tracing
     * @param servletRequest
     * @param servletResponse
     * @param chain
     * @param request
     */
    private void doTracingFiltere(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain, HttpServletRequest request) {
        Tracer tracer = GlobalTracer.get();
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(request.getRequestURI());
        try {
            // 把header里的span信息和指定的map信息读取
            Map<String, String> hMap = TracerUtils.parseTraceContext(request);
            SpanContext parentSpan = tracer.extract(Format.Builtin.HTTP_HEADERS, new TextMapAdapter(hMap));
            if (parentSpan != null) {
                spanBuilder.asChildOf(parentSpan);
            }
        } catch (IllegalArgumentException e) {
            spanBuilder.withTag("Error", "extract from request fail, error msg:" + e.getMessage());
            logger.error("解析trace context出现异常", e);
        }


        // set current activate span
        Span span = spanBuilder.start();
        MDCUtil.putTraceId(span.context().toTraceId());

        try (Scope scope = tracer.scopeManager().activate(span)) {
            Tags.HTTP_URL.set(span, request.getRequestURI());
            Tags.HTTP_METHOD.set(span, request.getMethod());
            TracerUtils.setClue(span);
            span.setTag(HeaderConstant.REQUEST_FROM, request.getHeader(HeaderConstant.REQUEST_FROM));
            span.setTag(HeaderConstant.REQUEST_ADDRESS, request.getHeader(HeaderConstant.REQUEST_ADDRESS));
            chain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            TracerUtils.reportErrorTrace(e);
            logger.error(e.getMessage(), e);
        } finally {
            span.finish();
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * 是否是需要排除监控的url,如果是,则不需要trace监控
     * @param URI
     * @return
     */
    private Boolean isExcludeURI(String URI) {
        return URI.startsWith(actuatorPrefix)
                || URI.startsWith(swaggerPrefix)
                || URI.startsWith(webjarsPrefix)
                || URI.startsWith(docPrefix);
    }

}