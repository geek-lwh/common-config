package com.aha.tech.interceptor;

import com.aha.tech.annotation.XEnv;
import com.aha.tech.constants.CatConstantsExt;
import com.aha.tech.filter.cat.CatContext;
import com.aha.tech.model.XEnvDto;
import com.aha.tech.threadlocal.CatContextThreadLocal;
import com.aha.tech.threadlocal.XEnvThreadLocal;
import com.dianping.cat.Cat;
import com.google.common.collect.Lists;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;

/**
 * @Author: luweihong
 * @Date: 2018/12/3
 * <p>
 * feign request 拦截器
 * 用于动态传递header信息,如果有需要需要自己定义
 */
public class FeignRequestInterceptor implements RequestInterceptor {

    private final Logger logger = LoggerFactory.getLogger(FeignRequestInterceptor.class);

    private static final String USERNAME = "visitor";

    private static final String PASSWORD = "28ad87ef9fdce5d12dea093b860e8772";

    private static final String BASE64_ENCODE = new String(Base64.encodeBase64(String.format("%s:%s", USERNAME, PASSWORD).getBytes()));

    public static final String CONTENT_TYPE = "Content-Type";

    public static final String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";

    public static final String CONNECTION = "Connection";

    private static final String HTTP_HEADER_CONNECTION_VALUE = "keep-alive";

    private static final String HTTP_HEADER_X_REQUESTED_WITH_KEY = "X-Requested-With";

    private static final String HTTP_HEADER_X_REQUESTED_WITH_VALUE = "XMLHttpRequest";

    private static final String HTTP_HEADER_KEEP_ALIVE_KEY = "Keep-Alive";

    private static final String HTTP_HEADER_KEEP_ALIVE_VALUE = "timeout=60";

    public static final String CONTENT_ENCODING = "Content-Encoding";

    public static final String CHARSET_ENCODING = StandardCharsets.UTF_8.displayName();

    public static final String X_TOKEN_KEY = "X-TOKEN";

    public static final String X_TOKEN = "3p1vN4urMs1X2fyMUM0KkhlwoIms04";

    public static final String ACCEPT = "accept";

    public static final String PROFILE = System.getProperty("spring.profiles.active", "prod");

    public static final String TEST_PROFILE_PREFIX = "test";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        initRequestHeader(requestTemplate);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
//        copyOriginalRequestHeader(attributes, requestTemplate);
        overwriteXenv(requestTemplate);
        buildTrace(requestTemplate);
        feignRequestLogging(requestTemplate);
    }

    private void overwriteXenv(RequestTemplate requestTemplate) {
        XEnvDto xEnvDto = XEnvThreadLocal.get();
        if (xEnvDto != null) {
            initHeaderFromEnv(xEnvDto, requestTemplate);
        }
    }

    /**
     * 复制原始请求头
     * @param attributes
     * @param requestTemplate
     */
    private void copyOriginalRequestHeader(ServletRequestAttributes attributes, RequestTemplate requestTemplate) {
        HttpServletRequest request = attributes.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String k = headerNames.nextElement();
            String v = request.getHeader(k);
            requestTemplate.header("Original_" + k, v);
        }
    }

    /**
     * 构建调用链路
     * @param requestTemplate
     */
    private void buildTrace(RequestTemplate requestTemplate) {
        CatContext catContext = CatContextThreadLocal.get();
        if (catContext == null) {
            return;
        }
        Cat.logRemoteCallClient(catContext, Cat.getManager().getDomain());
        String rootId = catContext.getProperty(Cat.Context.ROOT);
        String parentId = catContext.getProperty(Cat.Context.PARENT);
        String childId = catContext.getProperty(Cat.Context.CHILD);

        requestTemplate.header(CatConstantsExt.CAT_HTTP_HEADER_ROOT_MESSAGE_ID, rootId);
        requestTemplate.header(CatConstantsExt.CAT_HTTP_HEADER_PARENT_MESSAGE_ID, parentId);
        requestTemplate.header(CatConstantsExt.CAT_HTTP_HEADER_CHILD_MESSAGE_ID, childId);
        requestTemplate.header(CatConstantsExt.APPLICATION_NAME, Cat.getManager().getDomain());

        MDC.put("traceId", parentId);
        logger.info(Cat.getManager().getDomain() + "开始Feign远程调用 : " + requestTemplate.method() + " 消息模型 : rootId = " + rootId + " parentId = " + parentId + " childId = " + childId);
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
        requestTemplate.header(ACCEPT, acceptableMediaTypes);
        requestTemplate.header(CONNECTION, HTTP_HEADER_CONNECTION_VALUE);
        requestTemplate.header(HTTP_HEADER_KEEP_ALIVE_KEY, HTTP_HEADER_KEEP_ALIVE_VALUE);
        requestTemplate.header(HTTP_HEADER_X_REQUESTED_WITH_KEY, HTTP_HEADER_X_REQUESTED_WITH_VALUE);
        requestTemplate.header(CONTENT_ENCODING, CHARSET_ENCODING);
        requestTemplate.header(X_TOKEN_KEY, X_TOKEN);
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

        logger.info("Feign request Info : {}", sb);
    }
}
