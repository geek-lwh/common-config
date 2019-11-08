package com.aha.tech.config.swagger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;


@Configuration
@ConditionalOnProperty(name = "use.common.swagger")
@EnableSwagger2
public class SwaggerConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerConfiguration.class);


    @Value("${common.swagger.namespace:unKnown}")
    private String namespace;

    @Value("${common.swagger.scan.package:com.aha.tech.controller}")
    private String scanPackage;

    @Value("${common.swagger.title:API文档}")
    private String title;

    @Value("${common.swagger.description:java服务}")
    private String description;

    @Value("${swagger.version:2.0}")
    private String version;

    @Bean
    public Docket hjmServerApi() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2).groupName(namespace)
                .apiInfo(apiInfo())
                .select().apis(or(RequestHandlerSelectors.basePackage(scanPackage)))
                .paths(PathSelectors.any()).build();
        logger.info("swagger docket init finish >> {}", docket);

        return docket;
    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfoBuilder = new ApiInfoBuilder().title(title).description(description)
                .version(version)// 版本显示
                .build();

        logger.info("swagger apiInfoBuilder init finish >> {}", apiInfoBuilder);
        return apiInfoBuilder;
    }
}
