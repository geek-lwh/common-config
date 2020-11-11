//package com.aha.tech.filter;
//
//import com.aha.tech.constant.CatConstant;
//import com.aha.tech.constant.HeaderConstant;
//import com.aha.tech.constant.OrderedConstant;
//import com.aha.tech.filter.tracer.CatContext;
//import com.aha.tech.threadlocal.CatContextThreadLocal;
//import com.dianping.cat.Cat;
//import com.dianping.cat.message.Transaction;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//
///**
// * @Author: luweihong
// * @Date: 2019/11/19
// */
//@ConditionalOnProperty(name = "use.common.cat")
//@Component
//@Order(OrderedConstant.CAT_CONTEXT_FILTER_ORDERED)
//@WebFilter(filterName = "CatContextServletFilter", urlPatterns = "/*")
//public class CatContextServletFilter implements Filter {
//
//    private final Logger logger = LoggerFactory.getLogger(CatContextServletFilter.class);
//
//    @Override
//    public void init(FilterConfig filterConfig) {
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        String hasRoot = request.getHeader(CatConstant.CAT_HTTP_HEADER_ROOT_MESSAGE_ID);
//        String type = StringUtils.isBlank(hasRoot) ? CatConstant.CROSS_CONSUMER : CatConstant.CROSS_SERVER;
//        Transaction t = Cat.newTransaction(type, request.getRequestURI());
//        CatContext catContext = new CatContext();
//        try {
//            if (!StringUtils.isBlank(hasRoot)) {
//                String consumerServerName = request.getHeader(HeaderConstant.CONSUMER_SERVER_NAME);
//                String consumerServerHost = request.getHeader(HeaderConstant.CONSUMER_SERVER_HOST);
//                createRpcServerCross(consumerServerName, consumerServerHost);
//                catContext.addProperty(Cat.Context.ROOT, request.getHeader(CatConstant.CAT_HTTP_HEADER_ROOT_MESSAGE_ID));
//                catContext.addProperty(Cat.Context.PARENT, request.getHeader(CatConstant.CAT_HTTP_HEADER_PARENT_MESSAGE_ID));
//                catContext.addProperty(Cat.Context.CHILD, request.getHeader(CatConstant.CAT_HTTP_HEADER_CHILD_MESSAGE_ID));
//                Cat.logRemoteCallServer(catContext);
//            }
//
//            CatContextThreadLocal.set(catContext);
//            filterChain.doFilter(servletRequest, servletResponse);
//            t.setStatus(Transaction.SUCCESS);
//        } catch (Exception ex) {
//            t.setStatus(ex);
//            Cat.logError(ex);
//        } finally {
//            CatContextThreadLocal.remove();
//            t.complete();
//        }
//    }
//
//    /**
//     * 创建rpcServer端链路
//     * @param consumerServerName
//     */
//    private void createRpcServerCross(String consumerServerName, String consumerServerHost) {
//        if (StringUtils.isBlank(consumerServerName)) {
//            consumerServerName = CatConstant.UNKNOWN_SERVER;
//        }
//        Cat.logEvent(CatConstant.PROVIDER_CALL_APP, consumerServerName);
//        Cat.logEvent(CatConstant.PROVIDER_CALL_SERVER, consumerServerHost);
//    }
//
//    @Override
//    public void destroy() {
//    }
//
//}