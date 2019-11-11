package com.aha.tech.config.http;

import com.aha.tech.config.http.interceptor.HttpInterceptor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: monkey
 * @Date: 2018/7/29
 */
@Configuration
@ConditionalOnProperty(name = "use.common.http", matchIfMissing = true)
public class RestTemplateConfiguration {

    @Resource
    private CloseableHttpClient defaultCloseableHttpClient;

    @Resource
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Resource
    private Runnable idleConnectionMonitor;

    /**
     * restTemplate 使用jackson做httpMessageConverter 拦截器打印request和response的信息
     */
    @Primary
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        // 添加数据转换器,目前只支持string json
        StringHttpMessageConverter stringHttpMessageConverter =
                new StringHttpMessageConverter(Charset.forName("utf-8"));
        ArrayList<MediaType> arrayList = new ArrayList<>(2);
        arrayList.add(MediaType.APPLICATION_JSON);
        arrayList.add(MediaType.APPLICATION_OCTET_STREAM);
        arrayList.add(MediaType.TEXT_HTML);

        arrayList.add(MediaType.TEXT_PLAIN);

        stringHttpMessageConverter.setSupportedMediaTypes(arrayList);
        messageConverters.add(stringHttpMessageConverter);
        messageConverters.add(mappingJackson2HttpMessageConverter);

        messageConverters.add(new FormHttpMessageConverter());

        restTemplate.setMessageConverters(messageConverters);

        restTemplate.setInterceptors(Collections.singletonList(new HttpInterceptor()));
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(clientHttpRequestFactory()));
//        RestTemplateUtil.setRestTemplate(restTemplate);
        return restTemplate;
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(defaultCloseableHttpClient);
        return clientHttpRequestFactory;
    }

    /**
     * 定期清理 http pool idle resource
     */
    @PostConstruct
    public void cleanHttpPoolIdleResourceScheduler() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleWithFixedDelay(idleConnectionMonitor, 1, 10, TimeUnit.SECONDS);
    }

}
