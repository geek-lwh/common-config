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

import javax.sql.DataSource;

/**
 * @Author: luweihong
 * @Date: 2019/11/07
 *
 * use.common.jdbc.datasource.enable 不配置默认使用基础组件提供的数据源配置
 */
@Configuration
@ConditionalOnProperty(name = "use.common.jdbc", matchIfMissing = true)
public class DataSourceConfiguration {

    public final Logger logger = LoggerFactory.getLogger(DataSourceConfiguration.class);

    @Value("${common.jdbc.driverClassName:com.mysql.jdbc.Driver}")
    private String driverClassName;

    @Value("${common.jdbc.jdbcUrl}")
    private String jdbcUrl;

    @Value("${common.jdbc.username}")
    private String username;

    @Value("${common.jdbc.password}")
    private String password;

    @Value("${common.jdbc.connectionTimeout:5000}")
    private Long connectionTimeout;

    @Value("${common.jdbc.idleTimeout:600000}")
    private Long idleTimeout;

    @Value("${common.jdbc.maximumPoolSize:50}")
    private Integer maximumPoolSize;

    @Value("${common.jdbc.minimumIdle:10}")
    private Integer minimumIdle;

    @Primary
    @Bean(name = "dataSource")
    public DataSource dataSource() {
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
        logger.info("数据源加载完成 url : {}, connectionTimeout : {}, idleTimeout : {}, maximumPoolSize : {},minimumIdle : {}", jdbcUrl, connectionTimeout, idleTimeout, maximumPoolSize, minimumIdle);
        return hikariDataSource;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }
}
