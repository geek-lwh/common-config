package com.aha.tech.config.web;

import com.aha.tech.config.jackson.JacksonConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: luweihong
 * @Date: 2018/7/26
 * spring boot 2.0 不推荐使用extend继承web support等类,会导致某些组件被覆盖
 * 也不推荐使用@EnableMVC等标签让项目编程web应用
 * 使用实现webmvc
 */
@Configuration
@AutoConfigureAfter(JacksonConfiguration.class)
public class WebConfiguration implements WebMvcConfigurer {

    @Resource
    private ObjectMapper objectMapper;

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        return jsonConverter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(mappingJackson2HttpMessageConverter());
    }
}
