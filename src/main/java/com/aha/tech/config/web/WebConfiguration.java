package com.aha.tech.config.web;

import com.aha.tech.interceptor.EnvInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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
@ConditionalOnProperty(name = "use.common.web", matchIfMissing = true)
public class WebConfiguration implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfiguration.class);

    @Resource
    private ObjectMapper objectMapper;

    @Value("${common.server.tomcat.contextPath:/}")
    private String contextPath;

    @Primary
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        return jsonConverter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter webMessageConverter = mappingJackson2HttpMessageConverter();
//        List<MediaType> supportedMediaTypes = new ArrayList<>();
//        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
//        supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
//        supportedMediaTypes.add(MediaType.TEXT_PLAIN);
//        webMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
        converters.add(webMessageConverter);

        logger.info("web http message converter init finish >> {} ", webMessageConverter);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] excludePathPatterns;
        if (!contextPath.equals("/")) {
            excludePathPatterns = new String[]{"/**/swagger-ui.html/**", "/**/webjars/**", "/**/swagger-resources/**", "/**/actuator/prometheus"};
        } else {
            excludePathPatterns = new String[]{"/swagger-ui.html/**", "/webjars/**", "/swagger-resources/**", "/actuator/prometheus/**"};
        }

        registry.addInterceptor(new EnvInterceptor())
                .addPathPatterns("/**").excludePathPatterns(excludePathPatterns);

    }

    /**
     * 跨域
     * @param registry
     */
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").
                allowedOrigins("*"). //允许跨域的域名，可以用*表示允许任何域名使用
                allowedMethods("POST", "PUT", "GET", "DELETE"). //允许任何方法（post、get等）
                allowedHeaders("*"). //允许任何请求头
                maxAge(3600L); //maxAge
    }

}
