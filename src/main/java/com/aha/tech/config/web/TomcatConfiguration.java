package com.aha.tech.config.web;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;
import java.nio.charset.StandardCharsets;

/**
 * @Author: luweihong
 * @Date: 2019/11/11
 *
 *
 */
@Configuration
@ConditionalOnProperty(name = "use.common.tomcat.server")
public class TomcatConfiguration {

    private Logger logger = LoggerFactory.getLogger(TomcatConfiguration.class);

    // 端口号
    @Value("${common.server.tomcat.port}")
    private String port;

    // contextPath
    @Value("${common.server.tomcat.contextPath:/}")
    private String contextPath;

    // 指定当所有可以使用的处理请求的线程数都被使用时，可以放到处理队列中的请求数，超过这个数的请求将不予处理
    @Value("${common.server.tomcat.acceptorThreadCount:200}")
    private String acceptorThreadCount;

    // 最小空闲线程数：Tomcat初始化时创建的线程数,没有人使用也开这么多空线程等待
    @Value("${common.server.tomcat.minSpareThreads:20}")
    private String minSpareThreads;

    // 一旦创建的线程超过这个值，Tomcat就会关闭不再需要的socket线程
    @Value("${common.server.tomcat.maxSpareThreads:50}")
    private String maxSpareThreads;

    // tomcat可用于请求处理的最大线程数，默认是200
    @Value("${common.server.tomcat.maxThreads:400}")
    private String maxThreads;

    // 最大连接数
    @Value("${common.server.tomcat.maxConnections:10000}")
    private String maxConnections;

    // 利用java 异步io处理 nio方式
    @Value("${common.server.tomcat.protocol:org.apache.coyote.http11.Http11Nio2Protocol}")
    private String protocol;

    // 当强制要求https而请求是http时，重定向至端口号为443
    @Value("${common.server.tomcat.redirectPort:443}")
    private String redirectPort;

    // 给Tomcat配置gzip压缩(HTTP压缩)功能
    @Value("${common.server.tomcat.compression:on}")
    private String compression;

    // 报文超过多少压缩
    @Value("${common.server.tomcat.compressionMinSize:2048}")
    private String compressionMinSize;

    // 链接超时时间
    @Value("${common.server.tomcat.connectionTimeout:10000}")
    private String connectionTimeout;

    // 上传附件大小
    @Value("${common.server.tomcat.maxFileSize:512MB}")
    private String maxFileSize;

    // 请求大小
    @Value("${common.server.tomcat.maxRequestSize:10MB}")
    private String maxRequestSize;

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        TomcatConnectionCustomizer tomcatConnectionCustomizer = new TomcatConnectionCustomizer();
        tomcat.addConnectorCustomizers(tomcatConnectionCustomizer);
        if(!contextPath.isEmpty() && !"/".equals(contextPath)){
            tomcat.setContextPath(contextPath);
        }

        tomcat.setUriEncoding(StandardCharsets.UTF_8);
        logger.info("tomcatConnectionCustomizer init finish >> {}", tomcatConnectionCustomizer);
        return tomcat;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //  单个数据大小
        factory.setMaxFileSize(maxFileSize); // KB,MB
        /// 总上传数据大小
        factory.setMaxRequestSize(maxRequestSize);
        return factory.createMultipartConfig();
    }

    /**
     *
     * 默认http连接
     *
     * @version
     * @author lwh
     *
     */
    public class TomcatConnectionCustomizer implements TomcatConnectorCustomizer {

        public TomcatConnectionCustomizer() {
        }

        @Override
        public void customize(Connector connector) {
            connector.setPort(Integer.valueOf(port));
            connector.setAttribute("connectionTimeout", connectionTimeout);
            connector.setAttribute("acceptorThreadCount", acceptorThreadCount);
            connector.setAttribute("minSpareThreads", minSpareThreads);
            connector.setAttribute("maxSpareThreads", maxSpareThreads);
            connector.setAttribute("maxThreads", maxThreads);
            connector.setAttribute("maxConnections", maxConnections);
            connector.setAttribute("protocol", protocol);
            connector.setAttribute("redirectPort", redirectPort);
            connector.setAttribute("compression", compression);
            connector.setAttribute("compressionMinSize", compressionMinSize);
        }
    }

}