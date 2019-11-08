package com.aha.tech.config.mybatis;

import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import tk.mybatis.spring.annotation.MapperScan;

import javax.annotation.Resource;
import javax.sql.DataSource;


/**
 * @Author: luweihong
 * @Date: 2018/7/27
 */
@Configuration
@ConditionalOnProperty(name = "use.common.jdbc")
@MapperScan(basePackages = {"com.aha.tech.repository.dao.readwrite"}, sqlSessionFactoryRef = "sqlSessionFactory")
public class MybatisConfiguration {

    private  final Logger logger = LoggerFactory.getLogger(MybatisConfiguration.class);

    @Resource
    private DataSource dataSource;

    @Resource
    private PageInterceptor pageInterceptor;


    @Primary
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/readwrite/*Mapper.xml"));

        bean.setPlugins(new Interceptor[]{pageInterceptor});

        logger.info("sql session factory init finish >> {}",bean);
        return bean.getObject();
    }


    @Primary
    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate readwriteSqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
        logger.info("sql session factory init finish >> {}",sqlSessionFactory);

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
        logger.info("datasource transaction famanager init finish >> {}",transactionManager);

        return transactionManager;
    }

}
