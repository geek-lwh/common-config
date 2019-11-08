package com.aha.tech.config.mybatis.Interceptor;

import com.github.pagehelper.PageInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @Author: monkey
 * @Date: 2018/7/28
 * 将一些公共的代码,不易区分边界,又重复使用的代码聚合在此
 * 方便复用
 */
@Configuration
@ConditionalOnProperty(name = "use.common.jdbc")
public class PageInterceptorFactory {

    private static final Logger logger = LoggerFactory.getLogger(PageInterceptor.class);

    /**
     * mybatis 分页插件
     * readwrite,read库使用
     * @return
     */
    @Bean
    public PageInterceptor pageInterceptor() {
        PageInterceptor pageInterceptor = new com.github.pagehelper.PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        pageInterceptor.setProperties(properties);

        return pageInterceptor;
    }

}
