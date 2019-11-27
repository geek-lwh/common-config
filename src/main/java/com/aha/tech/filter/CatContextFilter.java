package com.aha.tech.filter;

import com.aha.tech.filter.cat.CatContext;
import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Transaction;
import org.slf4j.MDC;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Properties;

/**
 * @Author: luweihong
 * @Date: 2019/11/19
 */
public class CatContextFilter implements Filter {

    public static final String CAT_HTTP_HEADER_CHILD_MESSAGE_ID = "X-CAT-CHILD-MESSAGE-ID";

    public static final String CAT_HTTP_HEADER_PARENT_MESSAGE_ID = "X-CAT-PARENT-MESSAGE-ID";

    public static final String CAT_HTTP_HEADER_ROOT_MESSAGE_ID = "X-CAT-ROOT-MESSAGE-ID";
//
//    public static String applicationName = "";
//
//    static {
//        try {
//            Properties properties = PropertiesLoaderUtils.loadAllProperties("application.properties");
//            applicationName = properties.getProperty("app.id", "default");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        CatContext catContext = new CatContext();
        catContext.addProperty(Cat.Context.ROOT, request.getHeader(CAT_HTTP_HEADER_ROOT_MESSAGE_ID));
        catContext.addProperty(Cat.Context.PARENT, request.getHeader(CAT_HTTP_HEADER_PARENT_MESSAGE_ID));
        catContext.addProperty(Cat.Context.CHILD, request.getHeader(CAT_HTTP_HEADER_CHILD_MESSAGE_ID));
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