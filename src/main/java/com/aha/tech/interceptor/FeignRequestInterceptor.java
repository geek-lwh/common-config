package com.aha.tech.interceptor;

import com.aha.tech.annotation.XEnv;
import com.aha.tech.constant.CatConstant;
import com.aha.tech.constant.HeaderConstant;
import com.aha.tech.filter.cat.CatContext;
import com.aha.tech.model.XEnvDto;
import com.aha.tech.threadlocal.CatContextThreadLocal;
import com.aha.tech.threadlocal.XEnvThreadLocal;
import com.aha.tech.util.IpUtil;
import com.aha.tech.util.MDCUtil;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Lists;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;

import static com.aha.tech.constant.HeaderConstant.*;

/**
 * @Author: luweihong
 * @Date: 2018/12/3
 * <p>
 * feign request 拦截器
 * 用于动态传递header信息,如果有需要需要自己定义
 */
public class FeignRequestInterceptor implements RequestInterceptor {

    private final Logger logger = LoggerFactory.getLogger(FeignRequestInterceptor.class);

    @Override
    public void apply(RequestTemplate requestTemplate) {
        initRequestHeader(requestTemplate);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        Transaction t = Cat.newTransaction(CatConstant.CROSS_CONSUMER, requestTemplate.url());
        try {
            int port = attributes.getRequest().getServerPort();
            overwriteXenv(requestTemplate);
            createRpcClient(requestTemplate, port, t);
            feignRequestLogging(requestTemplate);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            logger.error("初始化rpcClient异常", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
    }

    private void overwriteXenv(RequestTemplate requestTemplate) {
        XEnvDto xEnvDto = XEnvThreadLocal.get();
        if (xEnvDto != null) {
            initHeaderFromEnv(xEnvDto, requestTemplate);
        }
    }

    /**
     * 构建调用链路
     * @param requestTemplate
     */
    private void createRpcClient(RequestTemplate requestTemplate, int port, Transaction t) throws Exception {
        CatContext catContext = CatContextThreadLocal.get();
        if (catContext == null) {
            return;
        }


        createRpcClientCross(t, port);
        Cat.logRemoteCallClient(catContext, Cat.getManager().getDomain());
        requestTemplate.header(CatConstant.CAT_HTTP_HEADER_ROOT_MESSAGE_ID, catContext.getProperty(Cat.Context.ROOT));
        requestTemplate.header(CatConstant.CAT_HTTP_HEADER_PARENT_MESSAGE_ID, catContext.getProperty(Cat.Context.PARENT));
        requestTemplate.header(CatConstant.CAT_HTTP_HEADER_CHILD_MESSAGE_ID, catContext.getProperty(Cat.Context.CHILD));
        requestTemplate.header(HeaderConstant.CONSUMER_SERVER_NAME, Cat.getManager().getDomain());
//        requestTemplate.header(HeaderConstant.CONSUMER_SERVER_PORT, String.valueOf(port));
        String localAddress = IpUtil.getLocalHostAddress();
        requestTemplate.header(HeaderConstant.CONSUMER_SERVER_HOST, localAddress + ":" + port);
    }

    /**
     * 从xEnvDto中解析值到feign requestHeader
     * @param xEnvDto
     * @param requestTemplate
     */
    private void initHeaderFromEnv(XEnvDto xEnvDto, RequestTemplate requestTemplate) {
        try {
            Field[] xEnvFields = xEnvDto.getClass().getDeclaredFields();
            for (Field field : xEnvFields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(XEnv.class)) {
                    XEnv annotation = field.getAnnotation(XEnv.class);
                    String headerName = annotation.value();
                    PropertyDescriptor pd = new PropertyDescriptor(field.getName(), xEnvDto.getClass());
                    Method readMethod = pd.getReadMethod();
                    if (readMethod != null) {
                        Object getValue = readMethod.invoke(xEnvDto);
                        if (getValue != null) {
                            requestTemplate.header(headerName, getValue.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 初始化请求头
     * @param requestTemplate
     */
    private void initRequestHeader(RequestTemplate requestTemplate) {
        if (!requestTemplate.headers().containsKey(CONTENT_TYPE)) {
            String contentType = requestTemplate.method().equals(HttpMethod.POST.name()) ? MediaType.APPLICATION_JSON_UTF8_VALUE : MediaType.TEXT_PLAIN_VALUE;
            requestTemplate.header(CONTENT_TYPE, contentType);
        }

        List<String> acceptableMediaTypes = Lists.newArrayList(MediaType.ALL_VALUE);
        requestTemplate.header(HeaderConstant.ACCEPT, acceptableMediaTypes);
        requestTemplate.header(HeaderConstant.CONNECTION, HTTP_HEADER_CONNECTION_VALUE);
        requestTemplate.header(HeaderConstant.HTTP_HEADER_KEEP_ALIVE_KEY, HTTP_HEADER_KEEP_ALIVE_VALUE);
        requestTemplate.header(HeaderConstant.HTTP_HEADER_X_REQUESTED_WITH_KEY, HTTP_HEADER_X_REQUESTED_WITH_VALUE);
        requestTemplate.header(HeaderConstant.CONTENT_ENCODING, CHARSET_ENCODING);
        requestTemplate.header(HeaderConstant.X_TOKEN_KEY, X_TOKEN);
        requestTemplate.header(HeaderConstant.TRACE_ID, MDCUtil.getTraceId());
    }

    /**
     * feign调用日志
     * @param requestTemplate
     */
    private void feignRequestLogging(RequestTemplate requestTemplate) {
        StringBuilder sb = new StringBuilder(System.lineSeparator());
        sb.append("Feign request URI : ").append(requestTemplate.url()).append(requestTemplate.queryLine()).append(System.lineSeparator());
        sb.append("Feign request HEADER : ").append(requestTemplate.headers().toString()).append(System.lineSeparator());
        String body = requestTemplate.body() == null ? Strings.EMPTY : new String(requestTemplate.body(), Charset.forName("utf-8"));
        sb.append("Feign request BODY : ").append(body);

        logger.info("Feign request INFO : {}", sb);
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
     * event complete
     * @param event
     */
    private void completeEvent(Event event) {
        event.complete();
    }

    /**
     * 复制原始请求头
     * @param attributes
     * @param requestTemplate
     */
    @Deprecated
    private void copyOriginalRequestHeader(ServletRequestAttributes attributes, RequestTemplate requestTemplate) {
        HttpServletRequest request = attributes.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String k = headerNames.nextElement();
            String v = request.getHeader(k);
            requestTemplate.header("Original_" + k, v);
        }
    }

}
