package com.aha.tech.filter;

import com.aha.tech.constant.CatConstantsExt;
import com.aha.tech.filter.cat.CatContext;
import com.aha.tech.threadlocal.CatContextThreadLocal;
import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: luweihong
 * @Date: 2019/11/19
 */
public class CatContextServletFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        CatContext catContext = new CatContext();
        catContext.addProperty(Cat.Context.ROOT, request.getHeader(CatConstantsExt.CAT_HTTP_HEADER_ROOT_MESSAGE_ID));
        catContext.addProperty(Cat.Context.PARENT, request.getHeader(CatConstantsExt.CAT_HTTP_HEADER_PARENT_MESSAGE_ID));
        catContext.addProperty(Cat.Context.CHILD, request.getHeader(CatConstantsExt.CAT_HTTP_HEADER_CHILD_MESSAGE_ID));
        catContext.addProperty(CatConstantsExt.APPLICATION_NAME, Cat.getManager().getDomain());
        if (catContext.getProperty(Cat.Context.ROOT) == null) {
            // 当前项目的app.id
            Cat.logRemoteCallClient(catContext, Cat.getManager().getDomain());
        } else {
            Cat.logRemoteCallServer(catContext);
        }

        CatContextThreadLocal.set(catContext);
        Transaction t = Cat.newTransaction(CatConstants.TYPE_URL, request.getRequestURI());

        try {
            Cat.logEvent(CatConstantsExt.Type_URL_METHOD, request.getMethod(), Message.SUCCESS, request.getRequestURL().toString());
            Cat.logEvent(CatConstantsExt.Type_URL_CLIENT, request.getRemoteHost());

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
}