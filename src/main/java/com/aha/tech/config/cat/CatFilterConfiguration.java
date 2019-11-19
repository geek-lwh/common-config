package com.aha.tech.config.cat;

import com.aha.tech.filter.CatContextFilter;
import com.dianping.cat.Cat;
import com.dianping.cat.servlet.CatFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @Author: luweihong
 * @Date: 2019/11/19
 */
@Order(0)
@Configuration
@ConditionalOnProperty(name = "use.common.cat")
public class CatFilterConfiguration {

    @Bean
    public FilterRegistrationBean catFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        CatContextFilter filter = new CatContextFilter();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("cat-servlet-filter");
        registration.setOrder(1);
        return registration;
    }

}
