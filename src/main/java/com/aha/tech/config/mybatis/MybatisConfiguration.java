package com.aha.tech.config.mybatis;

import com.aha.tech.config.jdbc.JdbcConfiguration;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
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
@AutoConfigureAfter(JdbcConfiguration.class)
@MapperScan(basePackages = {"com.aha.tech.repository.dao.readwrite"}, sqlSessionFactoryRef = "sqlSessionFactory")
public class MybatisConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisConfiguration.class);

    @Resource
    private DataSource dataSource;

    @Resource
    private PageInterceptor pageInterceptor;


    @Primary
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        LOGGER.info("sqlSessionFactory init completed !");
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/readwrite/*Mapper.xml"));
        bean.setPlugins(new Interceptor[]{pageInterceptor});
        return bean.getObject();
    }


    @Primary
    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate readwriteSqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 事务配置
     * 直接@Transactional即可
     * @return
     */
    @Primary
    @Bean(name = "transactionManager")
    public DataSourceTransactionManager readwriteTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

}
