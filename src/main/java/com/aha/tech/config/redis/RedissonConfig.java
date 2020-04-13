package com.aha.tech.config.redis;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


/**
 * @Author: luweihong
 * @Date: 2019/11/8
 */
@Configuration
@ConditionalOnProperty(name = "use.common.redis")
public class RedissonConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedissonConfig.class);


    @Value("${common.redis.host:localhost}")
    public String host;

    @Value("${common.redis.port:6379}")
    public Integer port;

    @Value("${common.redis.password}")
    public String password;

    @Value("${common.redis.timeout:10000}")
    public Integer timeout;

    @Value("${common.redis.database:0}")
    public Integer database;

    @Value("${common.redis.max-active:50}")
    public Integer maxActive;

    @Value("${common.redis.max-wait:10}")
    public Integer maxWait;

    @Value("${common.redis.max-idle:15}")
    public Integer maxIdle;

    @Value("${common.redis.min-idle:5}")
    public Integer minIdle;

    @Primary
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setConnectionPoolSize(maxActive)
                .setConnectionMinimumIdleSize(maxIdle)
                .setDatabase(database)
                .setTimeout(timeout);
        if (StringUtils.isNotBlank(password)) {
            config.useSingleServer().setPassword(password);
        }

        logger.info("redisson数据源初始化完成 host : {} , port : {} , database : {} timeout : {}", host, port, database, timeout);
        return Redisson.create(config);
    }
}