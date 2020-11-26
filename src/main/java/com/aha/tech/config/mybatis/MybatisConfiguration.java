package com.aha.tech.config.mybatis;

import com.aha.tech.config.mybatis.plugin.PagePlugin;
import com.aha.tech.config.mybatis.plugin.TracePlugin;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;


/**
 * @Author: luweihong
 * @Date: 2018/7/27
 */
@Configuration
@ConditionalOnProperty(name = "use.common.jdbc")
public class MybatisConfiguration {

    private final Logger logger = LoggerFactory.getLogger(MybatisConfiguration.class);

    @Resource
    private DataSource dataSource;

    @Value("${jaeger.sampler.enable:true}")
    private Boolean enable;

    @Value("${common.jdbc.jdbcUrl}")
    private String jdbcUrl;


    @Primary
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/*Mapper.xml"));

        PageInterceptor pageInterceptor = new PagePlugin().pageInterceptor();
        TracePlugin tracerPlugin = new TracePlugin(jdbcUrl);
        if (enable) {
            sqlSessionFactoryBean.setPlugins(new Interceptor[]{pageInterceptor, tracerPlugin});
        } else {
            sqlSessionFactoryBean.setPlugins(new Interceptor[]{pageInterceptor});
        }

        return sqlSessionFactoryBean.getObject();
    }

    @Primary
    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);

        return sqlSessionTemplate;
    }

    /**
     * 事务配置
     * 直接@Transactional即可
     * @return
     */
    @Primary
    @Bean(name = "transactionManager")
    public DataSourceTransactionManager transactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        logger.info("hikari数据源事务加载完成");

        return transactionManager;
    }

}
