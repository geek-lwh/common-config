package com.aha.tech.filter;

import com.aha.tech.constant.CatConstant;
import com.aha.tech.constant.HeaderConstant;
import com.aha.tech.constant.OrderedConstant;
import com.aha.tech.filter.cat.CatContext;
import com.aha.tech.threadlocal.CatContextThreadLocal;
import com.aha.tech.util.IpUtil;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
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
        Transaction t = Cat.newTransaction(CatConstant.CROSS_SERVER, request.getRequestURI());
        try {
            CatContext catContext = new CatContext();
            if (null == request.getHeader(CatConstant.CAT_HTTP_HEADER_ROOT_MESSAGE_ID)) {
                int port = request.getServerPort();
                createRpcClientCross(t, port);
                Cat.logRemoteCallClient(catContext, Cat.getManager().getDomain());
            } else {
                String consumerServerName = request.getHeader(HeaderConstant.CONSUMER_SERVER_NAME);
                String consumerServerHost = request.getHeader(HeaderConstant.CONSUMER_SERVER_HOST);
                createRpcServerCross(consumerServerName, consumerServerHost, t);
                catContext.addProperty(Cat.Context.ROOT, request.getHeader(CatConstant.CAT_HTTP_HEADER_ROOT_MESSAGE_ID));
                catContext.addProperty(Cat.Context.PARENT, request.getHeader(CatConstant.CAT_HTTP_HEADER_PARENT_MESSAGE_ID));
                catContext.addProperty(Cat.Context.CHILD, request.getHeader(CatConstant.CAT_HTTP_HEADER_CHILD_MESSAGE_ID));
                Cat.logRemoteCallServer(catContext);
            }
            CatContextThreadLocal.set(catContext);
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

    /**
     * 创建RpcClient端链路
     * @param transaction
     * @param port
     */
    private void createRpcClientCross(Transaction transaction, int port) throws Exception {
        Event crossAppEvent = Cat.newEvent(CatConstant.CONSUMER_CALL_APP, Cat.getManager().getDomain());
        Event crossServerEvent = Cat.newEvent(CatConstant.CONSUMER_CALL_SERVER, IpUtil.getLocalHostAddress());
        Event crossPortEvent = Cat.newEvent(CatConstant.CONSUMER_CALL_PORT, String.valueOf(port));
        crossAppEvent.setStatus(Event.SUCCESS);
        crossServerEvent.setStatus(Event.SUCCESS);
        crossPortEvent.setStatus(Event.SUCCESS);
        completeEvent(crossAppEvent);
        completeEvent(crossPortEvent);
        completeEvent(crossServerEvent);
        transaction.addChild(crossAppEvent);
        transaction.addChild(crossPortEvent);
        transaction.addChild(crossServerEvent);
    }

    /**
     * 创建rpcServer端链路
     * @param consumerServerName
     * @param transaction
     */
    private void createRpcServerCross(String consumerServerName, String host, Transaction transaction) {
        Event crossAppEvent = Cat.newEvent(CatConstant.PROVIDER_CALL_APP, StringUtils.isBlank(consumerServerName) ? "UNKNOWN" : consumerServerName);
        Event crossServerEvent = Cat.newEvent(CatConstant.PROVIDER_CALL_SERVER, host);
        crossAppEvent.setStatus(Event.SUCCESS);
        crossServerEvent.setStatus(Event.SUCCESS);
        completeEvent(crossAppEvent);
        completeEvent(crossServerEvent);
        transaction.addChild(crossAppEvent);
        transaction.addChild(crossServerEvent);
    }

    /**
     * event complete
     * @param event
     */
    private void completeEvent(Event event) {
        event.complete();
    }

    @Override
    public void destroy() {

    }
}