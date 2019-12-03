package com.aha.tech.filter;

import com.aha.tech.filter.cat.CatContext;
import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Transaction;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Author: luweihong
 * @Date: 2019/11/19
 */
public class CatContextFilter implements Filter {

    public static final String X_TRACE_CHILD_ID = "x_trace_child_id";

    public static final String X_TRACE_PARENT_ID = "x_trace_parent_id";

    public static final String X_TRACE_ROOT_ID = "x-trace-id";

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        CatContext catContext = new CatContext();
        catContext.addProperty(Cat.Context.ROOT, request.getHeader(X_TRACE_ROOT_ID));
        catContext.addProperty(Cat.Context.PARENT, request.getHeader(X_TRACE_PARENT_ID));
        catContext.addProperty(Cat.Context.CHILD, request.getHeader(X_TRACE_CHILD_ID));
        if (catContext.getProperty(Cat.Context.ROOT) == null) {
            // 当前项目的app.id
            Cat.logRemoteCallClient(catContext, Cat.getManager().getDomain());
        } else {
            Cat.logRemoteCallServer(catContext);
        }

        MDC.put("traceId", catContext.getProperty(Cat.Context.ROOT));

        Transaction t = Cat.newTransaction(CatConstants.TYPE_URL, request.getRequestURI());
        try {
            filterChain.doFilter(servletRequest, servletResponse);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception ex) {
            t.setStatus(ex);
            Cat.logError(ex);
            throw ex;
        } finally {
            t.complete();
        }
    }

    @Override
    public void destroy() {

    }
}