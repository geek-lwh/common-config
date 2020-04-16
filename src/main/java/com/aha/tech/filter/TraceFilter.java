package com.aha.tech.filter;

import com.aha.tech.constant.OrderedConstant;
import com.aha.tech.util.MDCUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * luweihong
 */
@Component
@Order(OrderedConstant.TRACE_FILTER_ORDERED)
@WebFilter(filterName = "RequestResponseLogFilter", urlPatterns = "/*")
public class TraceFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TraceFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        MDCUtil.getAndSetTraceId(request);
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        MDC.clear();
    }
}



