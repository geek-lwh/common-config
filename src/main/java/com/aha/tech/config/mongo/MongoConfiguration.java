package com.aha.tech.config.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mapping.model.SnakeCaseFieldNamingStrategy;
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
 * 在application启动类上,排除 exclude={MongoAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class
 */
@Configuration
@ConditionalOnProperty(name = "use.common.mongo")
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Value("${common.mongo.connections.per.host:100}")
    private Integer connectionsPerHost;

    @Value("${common.mongo.min.connection.per.hots:50}")
    private Integer minConnectionsPerHost;

    // spring.data.mongodb.uri=mongodb://user:pwd@ip1:port1,ip2:port2/database
    @Value("${common.mongo.address:test}")
    private String address;

    @Value("${common.mongo.database.name:test}")
    private String databaseName;

    @Bean
    public MongoDbFactory mongoDbFactory() {


        // MongoDB地址列表
        List<ServerAddress> serverAddresses = new ArrayList<>();
        String[] hosts = address.split(",");
        for (String host : hosts) {
            String ipAddress = host.split(":")[0];
            Integer ipPort = Integer.parseInt(host.split(":")[1]);

            ServerAddress serverAddress = new ServerAddress(ipAddress, ipPort);
            serverAddresses.add(serverAddress);
        }

        System.out.println("serverAddresses:" + serverAddresses.toString());


        //创建客户端和Factory
        MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongoClient(), getDatabaseName());

        return mongoDbFactory;
    }

    @Override
    public MongoClient mongoClient() {
        //客户端配置（连接数、副本集群验证）
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        builder.connectionsPerHost(connectionsPerHost);
        builder.minConnectionsPerHost(minConnectionsPerHost);
        MongoClientOptions mongoClientOptions = builder.build();
        MongoClient mongoClient = new MongoClient(address, mongoClientOptions);

        return mongoClient;
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory(), mappingMongoConverter());
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter() throws Exception {

        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory());
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext());
        converter.setCustomConversions(customConversions());
        converter.setCodecRegistryProvider(mongoDbFactory());
        // 去掉class
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }

    @Bean
    public MongoMappingContext mongoMappingContext() throws ClassNotFoundException {
        MongoMappingContext mappingContext = new MongoMappingContext();
        mappingContext.setInitialEntitySet(getInitialEntitySet());
        mappingContext.setSimpleTypeHolder(customConversions().getSimpleTypeHolder());
        FieldNamingStrategy fieldNamingStrategy = new SnakeCaseFieldNamingStrategy();
        mappingContext.setFieldNamingStrategy(fieldNamingStrategy);
        mappingContext.setAutoIndexCreation(autoIndexCreation());

        return mappingContext;
    }

}
