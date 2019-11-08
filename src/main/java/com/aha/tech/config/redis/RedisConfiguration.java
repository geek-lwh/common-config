package com.aha.tech.config.redis;

import com.aha.tech.config.redis.serializer.FastJsonRedisSerializer;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;

/**
 * @Author: luweihong
 * @Date: 2019/11/08
 */
@Configuration
@ConditionalOnProperty(name = "use.common.redis")
public class RedisConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfiguration.class);

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
    @Bean(name = "redisConnectionFactory")
    public LettuceConnectionFactory publicRedisConnectionFactory() {
        LettuceClientConfiguration clientConfig =
                lettuceClientConfiguration(maxActive, maxIdle, minIdle, maxWait);
        RedisStandaloneConfiguration redisStandaloneConfiguration =
                redisStandaloneConfiguration(host, port, password, database);

        return createLettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
    }

    /**
     * 构建lettuceClientConfiguration
     *
     * @param maxActive
     * @param maxIdle
     * @param minIdle
     * @param maxWait
     * @return
     */
    private LettuceClientConfiguration lettuceClientConfiguration(Integer maxActive, Integer maxIdle, Integer minIdle, Integer maxWait) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(maxActive);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setMaxWaitMillis(maxWait);
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder().poolConfig(config);
        LettuceClientConfiguration clientConfig = builder.build();
        return clientConfig;
    }

    /**
     * 构建redis config
     *
     * @param host
     * @param port
     * @param password
     * @param database
     * @return
     */
    private RedisStandaloneConfiguration redisStandaloneConfiguration(String host, Integer port, String password, Integer database) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        config.setPassword(RedisPassword.of(password));
        config.setDatabase(database);

        logger.info("redis config init finish >> {}", config);
        return config;
    }

    /**
     * 构建redis工厂
     *
     * @param redisStandaloneConfiguration
     * @param clientConfiguration
     * @return
     */
    private LettuceConnectionFactory createLettuceConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration, LettuceClientConfiguration clientConfiguration) {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfiguration);
        logger.info("lettuce connection factory init finish >> {}", factory);

        return factory;
    }


    /**
     * 自定义redis序列化 使用fast json
     * @param redisConnectionFactory
     * @return
     */
    @Primary
    @Bean
    public RedisTemplate<String, Serializable> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        //设置序列化
        RedisTemplate<String, Serializable> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new FastJsonRedisSerializer(Object.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new FastJsonRedisSerializer(Object.class));
        template.setEnableDefaultSerializer(false);
        template.setConnectionFactory(redisConnectionFactory);

        logger.info("redis template init finish >. {}", template);
        return template;
    }

}