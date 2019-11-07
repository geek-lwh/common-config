package com.aha.tech.config.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;

/**
 * @Author: luweihong
 * @Date: 2019/11/07
 *
 * use.common.jdbc.datasource.enable 不配置默认使用基础组件提供的数据源配置
 */
@Order(value = Integer.MIN_VALUE)
@Configuration
@ConditionalOnProperty(name = "use.common.jdbc")
public class JdbcConfiguration {

    public static final Logger LOG = LoggerFactory.getLogger(JdbcConfiguration.class);

    @Value("${common.jdbc.driverClassName}")
    private String driverClassName;

    @Value("${common.jdbc.url}")
    private String jdbcUrl;

    @Value("${common.jdbc.username}")
    private String username;

    @Value("${common.jdbc.password}")
    private String password;

    @Value("${common.jdbc.connectionTimeout}")
    private Long connectionTimeout;

    @Value("${common.jdbc.idleTimeout}")
    private Long idleTimeout;

    @Value("${common.jdbc.maximumPoolSize}")
    private Integer maximumPoolSize;

    @Value("${common.jdbc.minimumIdle}")
    private Integer minimumIdle;

    @Primary
    @Bean(name = "dataSource")
    public DataSource dataSource() {
        LOG.info("============= begin init datasource params : {} =============", this);
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(driverClassName);
        hikariDataSource.setJdbcUrl(jdbcUrl);
        hikariDataSource.setUsername(username);
        hikariDataSource.setPassword(password);
        hikariDataSource.setConnectionTimeout(connectionTimeout);
        hikariDataSource.setIdleTimeout(idleTimeout);
        hikariDataSource.setMaximumPoolSize(maximumPoolSize);
        hikariDataSource.setMinimumIdle(minimumIdle);
        hikariDataSource.setPoolName("common-jdbc-pool");
        LOG.info("============= datasource init completed ! =============");
        return hikariDataSource;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }
}
