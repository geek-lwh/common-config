package com.aha.tech.interceptor;

import com.google.common.collect.Lists;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
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

    private static final String AUTHORIZATION_KEY = "Authorization";

    private static final String BASIC_AUTHORIZATION = String.format("Basic %s", BASE64_ENCODE);

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


    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String k = headerNames.nextElement();
            String v = request.getHeader(k);
            requestTemplate.header(k, v);
        }

//        CatContext catContext = new CatContext();
//        Cat.logRemoteCallClient(catContext,Cat.getManager().getDomain());
//
//        requestTemplate.header(CAT_HTTP_HEADER_ROOT_MESSAGE_ID,catContext.getProperty(Cat.Context.ROOT));
//        requestTemplate.header(CAT_HTTP_HEADER_PARENT_MESSAGE_ID,catContext.getProperty(Cat.Context.PARENT));
//        requestTemplate.header(CAT_HTTP_HEADER_CHILD_MESSAGE_ID,catContext.getProperty(Cat.Context.CHILD));


        requestTemplate.header(CONTENT_TYPE, APPLICATION_JSON_UTF8);
        List<String> acceptableMediaTypes = Lists.newArrayList(
                MediaType.APPLICATION_JSON.toString(),
                MediaType.APPLICATION_JSON_UTF8.toString(),
                MediaType.APPLICATION_XML.toString(),
                MediaType.TEXT_PLAIN.toString(),
                MediaType.APPLICATION_FORM_URLENCODED.toString(),
                MediaType.APPLICATION_OCTET_STREAM.toString());

        requestTemplate.header(ACCEPT, acceptableMediaTypes);
        requestTemplate.header(CONNECTION, HTTP_HEADER_CONNECTION_VALUE);
        requestTemplate.header(HTTP_HEADER_KEEP_ALIVE_KEY, HTTP_HEADER_KEEP_ALIVE_VALUE);
        requestTemplate.header(HTTP_HEADER_X_REQUESTED_WITH_KEY, HTTP_HEADER_X_REQUESTED_WITH_VALUE);
        requestTemplate.header(CONTENT_ENCODING, CHARSET_ENCODING);
        requestTemplate.header(X_TOKEN_KEY, X_TOKEN);

        StringBuilder sb = new StringBuilder(System.lineSeparator());
        sb.append("Feign request URI : ").append(requestTemplate.url()).append(System.lineSeparator());
        sb.append("Feign request HEADER : ").append(requestTemplate.headers().toString()).append(System.lineSeparator());
        sb.append("Feign request BODY : ").append(new String(requestTemplate.body(), Charset.forName("utf-8")));

        logger.info("Feign request Info : {}", sb);
    }
}

//        String token = request.getHeader("X-Token");
//        // 广告来源
//        String utmSource = request.getHeader("X-Env-Utm-Source");
//        // 广告媒介
//        String utmMedium = request.getHeader("X-Env-Utm-Medium");
//        // 广告名称
//        String utmCampaign = request.getHeader("X-Env-Utm-Campaign");
//        //
//        String utmTerm = request.getHeader("X-Env-Utm-Term");
//
//        String utmContent = request.getHeader("X-Env-Utm-Content");
//
//        String userId = request.getHeader("X-Env-User-Id");
//
//        String pk = request.getHeader("X-Env-PK");
//
//        String ps = request.getHeader("X-Env-PS");
//
//        String pd = request.getHeader("X-Env-PD");
//
//        String pp = request.getHeader("X-Env-PP");
//
//        String appType = request.getHeader("X-Env-App-Type");
//
//        String guniqid = request.getHeader("X-Env-Guniqid");
//
//        String channel = request.getHeader("X-Env-Channel");
//
//        String xForwarded = request.getHeader("X-Forwarded-For");
//
//        String userAgent = request.getHeader("User-Agent");
//
//        String os = request.getHeader("os");
//
//        String version = request.getHeader("version");