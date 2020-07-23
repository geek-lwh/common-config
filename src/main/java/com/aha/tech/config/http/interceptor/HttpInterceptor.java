package com.aha.tech.config.http.interceptor;

import com.google.common.collect.Lists;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @Author: monkey
 * @Date: 2018/7/29
 */
public class HttpInterceptor implements ClientHttpRequestInterceptor {

    private final Logger LOGGER = LoggerFactory.getLogger(HttpInterceptor.class);

    private final static String USERNAME = "visitor";

    private final static String PASSWORD = "28ad87ef9fdce5d12dea093b860e8772";

    private final static String BASE64_ENCODE = new String(Base64.encodeBase64(String.format("%s:%s", USERNAME, PASSWORD).getBytes()));

    private final static String BASIC_AUTHORIZATION = String.format("Basic %s", BASE64_ENCODE);

    private final static String HTTP_HEADER_CONNECTION_VALUE = "keep-alive";

    private final static String HTTP_HEADER_X_REQUESTED_WITH_KEY = "X-Requested-With";

    private final static String HTTP_HEADER_X_REQUESTED_WITH_VALUE = "XMLHttpRequest";

    private final static String HTTP_HEADER_KEEP_ALIVE_KEY = "Keep-Alive";

    private final static String HTTP_HEADER_KEEP_ALIVE_VALUE = "5000";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        addHeader(request);
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException {
        LOGGER.debug("===========================request begin================================================");
        LOGGER.debug("URI         : {}", request.getURI());
        LOGGER.debug("Method      : {}", request.getMethod());
        LOGGER.debug("Headers     : {}", request.getHeaders());
        LOGGER.debug("Request body: {}", new String(body, "UTF-8"));
        LOGGER.debug("==========================request end================================================");
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        LOGGER.debug("============================response begin==========================================");
        LOGGER.debug("Status code  : {}", response.getStatusCode());
        LOGGER.debug("Status text  : {}", response.getStatusText());
        LOGGER.debug("Headers      : {}", response.getHeaders());
        LOGGER.debug("Response body: {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
        LOGGER.debug("=======================response end=================================================");
    }

    /**
     * 拦截器添加Header 信息
     * @param request
     */
    private void addHeader(HttpRequest request) {
        HttpHeaders headers = request.getHeaders();

        List<MediaType> acceptableMediaTypes = Lists.newArrayList(
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML,
                MediaType.TEXT_PLAIN,
                MediaType.APPLICATION_JSON_UTF8,
                MediaType.APPLICATION_FORM_URLENCODED,
                MediaType.APPLICATION_OCTET_STREAM);


        if (!headers.containsKey(HttpHeaders.ACCEPT)) {
            headers.setAccept(acceptableMediaTypes);
        }

        if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        }

        if (!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            headers.add(HttpHeaders.AUTHORIZATION, BASIC_AUTHORIZATION);
        }

        if (!headers.containsKey(HttpHeaders.CONNECTION)) {
            headers.setConnection(HTTP_HEADER_CONNECTION_VALUE);
        }

        if (!headers.containsKey(HTTP_HEADER_X_REQUESTED_WITH_KEY)) {
            headers.add(HTTP_HEADER_X_REQUESTED_WITH_KEY, HTTP_HEADER_X_REQUESTED_WITH_VALUE);
        }

        if (!headers.containsKey(HTTP_HEADER_KEEP_ALIVE_KEY)) {
            headers.add(HTTP_HEADER_KEEP_ALIVE_KEY, HTTP_HEADER_KEEP_ALIVE_VALUE);
        }

        if (!headers.containsKey(HttpHeaders.CONTENT_ENCODING)) {
            headers.add(HttpHeaders.CONTENT_ENCODING, Charset.defaultCharset().name());
        }

    }

}
