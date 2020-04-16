package com.aha.tech.filter;

import com.aha.tech.constant.CatConstant;
import com.aha.tech.constant.HeaderConstant;
import com.aha.tech.constant.OrderedConstant;
import com.aha.tech.filter.cat.CatContext;
import com.aha.tech.threadlocal.CatContextThreadLocal;
import com.aha.tech.util.MDCUtil;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: luweihong
 * @Date: 2019/11/19
 */
@ConditionalOnProperty(name = "use.common.cat")
@Component
@Order(OrderedConstant.CAT_CONTEXT_FILTER_ORDERED)
@WebFilter(filterName = "CatContextServletFilter", urlPatterns = "/*")
public class CatContextServletFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        CatContext catContext = initCatContext(request);
        Cat.getManager().setTraceMode(true);

        Transaction t = Cat.newTransaction(CatConstant.CROSS_SERVER, request.getRequestURI());
        try {
            Cat.logEvent(CatConstant.PROVIDER_CALL_APP, catContext.getProperty(CatConstant.PROVIDER_APPLICATION_NAME));
            Cat.logEvent(CatConstant.PROVIDER_CALL_SERVER, catContext.getProperty(CatConstant.PROVIDER_APPLICATION_IP));
            Cat.logEvent(CatConstant.PROVIDER_TRACE_ID, MDCUtil.getTraceId());

            filterChain.doFilter(servletRequest, servletResponse);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception ex) {
            t.setStatus(ex);
            Cat.logError(ex);
        } finally {
            CatContextThreadLocal.remove();
            t.complete();
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * 初始化cat上下文
     * @param request
     * @return
     */
    private CatContext initCatContext(HttpServletRequest request) {
        CatContext catContext = new CatContext();
        catContext.addProperty(Cat.Context.ROOT, request.getHeader(CatConstant.CAT_HTTP_HEADER_ROOT_MESSAGE_ID));
        catContext.addProperty(Cat.Context.PARENT, request.getHeader(CatConstant.CAT_HTTP_HEADER_PARENT_MESSAGE_ID));
        catContext.addProperty(Cat.Context.CHILD, request.getHeader(CatConstant.CAT_HTTP_HEADER_CHILD_MESSAGE_ID));

        String callServerName = request.getHeader(HeaderConstant.CONSUMER_SERVER_NAME);
        if (StringUtils.isBlank(callServerName)) {
            callServerName = CatConstant.DEFAULT_APPLICATION_NAME;
        }
        catContext.addProperty(CatConstant.PROVIDER_APPLICATION_NAME, callServerName);

        String callServerIp = request.getHeader(HeaderConstant.CONSUMER_SERVER_IP);
        if (StringUtils.isBlank(callServerIp)) {
            callServerIp = request.getRemoteAddr();
        }
        catContext.addProperty(CatConstant.PROVIDER_APPLICATION_IP, callServerIp);


        if (catContext.getProperty(Cat.Context.ROOT) == null) {
            // 当前项目的app.id
            Cat.logRemoteCallClient(catContext, Cat.getManager().getDomain());
        } else {
            Cat.logRemoteCallServer(catContext);
        }

        CatContextThreadLocal.set(catContext);

        return catContext;
    }
}