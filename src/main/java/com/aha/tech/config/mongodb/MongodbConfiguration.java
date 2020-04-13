package com.aha.tech.config.mongodb;

import com.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: luweihong
 * @Date: 2019/12/6
 *
 */
@Configuration
@ConditionalOnProperty(name = "use.common.mongodb")
public class MongodbConfiguration extends AbstractMongoConfiguration {

    public final Logger logger = LoggerFactory.getLogger(MongodbConfiguration.class);


    @Value("${common.mongodb.host}")
    private String host;

    @Value("${common.mongodb.database}")
    private String database;

    @Value("${common.mongodb.username}")
    private String username;

    @Value("${common.mongodb.pwd}")
    private String pwd;

    @Value("${common.mongodb.connect.timeout:5000}")
    private Integer connectTimeout;

    @Value("${common.mongodb.socket.timeout:5000}")
    private Integer socketTimeout;

    @Value("${common.mongodb.max.wait.time:2000}")
    private Integer maxWaitTime;

    @Bean
    public MongoDbFactory mongoDbFactory() {
        return new SimpleMongoDbFactory(mongoClient(), getDatabaseName());
    }


    @Override
    public MongoClient mongoClient() {
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        builder.connectTimeout(connectTimeout);
        builder.socketTimeout(socketTimeout);
        builder.maxWaitTime(maxWaitTime);
        builder.writeConcern(WriteConcern.MAJORITY);
        MongoClientOptions mongoClientOptions = builder.build();

        if (StringUtils.isNotBlank(username)) {
            List<ServerAddress> serverAddressList = new ArrayList<>();
            String[] addressArray = host.split(",");
            for (String address : addressArray) {
                String[] uri = address.split(":");
                serverAddressList.add(new ServerAddress(uri[0], Integer.parseInt(uri[1])));
            }

            List<MongoCredential> mongoCredentialList = new ArrayList<>();
            mongoCredentialList.add(MongoCredential.createCredential(username, database, pwd.toCharArray()));
            return new MongoClient(serverAddressList, MongoCredential.createCredential(username, database, pwd.toCharArray()), mongoClientOptions);
        }

        logger.info("mongo数据源加载成功 host : {} , connectTimeout : {} , socketTimeout : {} , maxWaitTime : {} , writeConcern : {}", host, connectTimeout, socketTimeout, maxWaitTime, WriteConcern.MAJORITY);
        return new MongoClient(host, mongoClientOptions);
    }

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory(), mappingMongoConverter());
    }

    @Bean
    public MongoMappingContext mongoMappingContext() throws ClassNotFoundException {
        MongoMappingContext mappingContext = new MongoMappingContext();
        mappingContext.setInitialEntitySet(getInitialEntitySet());
        mappingContext.setSimpleTypeHolder(customConversions().getSimpleTypeHolder());
//        FieldNamingStrategy fieldNamingStrategy = new SnakeCaseFieldNamingStrategy();
//        mappingContext.setFieldNamingStrategy(fieldNamingStrategy);

        return mappingContext;
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter() throws Exception {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory());
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext());
        converter.setCustomConversions(customConversions());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }

}
