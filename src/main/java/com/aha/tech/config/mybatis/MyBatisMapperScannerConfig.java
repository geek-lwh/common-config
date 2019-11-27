//package com.aha.tech.config.mybatis;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.support.PropertiesLoaderUtils;
//import tk.mybatis.spring.mapper.MapperScannerConfigurer;
//
//import java.io.IOException;
//import java.util.Properties;
//
///**
// * @Author: luweihong
// * @Date: 2019/11/15
// */
//@Configuration
//public class MyBatisMapperScannerConfig {
//
//    @Bean
//    public MapperScannerConfigurer mapperScannerConfigurer() throws IOException {
//        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
//        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
//        mapperScannerConfigurer.setBasePackage("com.aha.tech.repository.dao.readwrite");
//
//        return mapperScannerConfigurer;
//    }
//}
