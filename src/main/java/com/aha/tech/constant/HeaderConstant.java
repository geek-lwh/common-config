package com.aha.tech.constant;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;

/**
 * @Author: luweihong
 * @Date: 2020/4/14
 */
public class HeaderConstant {

    public static final String USERNAME = "visitor";

    public static final String PASSWORD = "28ad87ef9fdce5d12dea093b860e8772";

    public static final String BASE64_ENCODE = new String(Base64.encodeBase64(String.format("%s:%s", USERNAME, PASSWORD).getBytes()));

    public static final String CONTENT_TYPE = "Content-Type";

    public static final String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";

    public static final String CONNECTION = "Connection";

    public static final String HTTP_HEADER_CONNECTION_VALUE = "keep-alive";

    public static final String HTTP_HEADER_X_REQUESTED_WITH_KEY = "X-Requested-With";

    public static final String HTTP_HEADER_X_REQUESTED_WITH_VALUE = "XMLHttpRequest";

    public static final String HTTP_HEADER_KEEP_ALIVE_KEY = "Keep-Alive";

    public static final String HTTP_HEADER_KEEP_ALIVE_VALUE = "5000";

    public static final String CONTENT_ENCODING = "Content-Encoding";

    public static final String CHARSET_ENCODING = StandardCharsets.UTF_8.displayName();

    public static final String X_TOKEN_KEY = "X-TOKEN";

    public static final String X_TOKEN = "3p1vN4urMs1X2fyMUM0KkhlwoIms04";

    public static final String ACCEPT = "accept";

    public static final String PROFILE = System.getProperty("spring.profiles.active", "prod");

    public static final String TEST_PROFILE_PREFIX = "test";

    public static final String TRACE_ID = "X-Trace-Id";

    public static final String CONSUMER_SERVER_NAME = "X-Consumer-Server-Name";

    public static final String CONSUMER_SERVER_IP = "X-Consumer-Server-Ip";
}
