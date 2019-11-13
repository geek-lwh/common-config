package com.aha.tech.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.AbstractMessageListenerContainer.AckMode;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author:luweihong
 * @Date:2019/11/11
 * @See https://www.cnblogs.com/rilley/p/5391268.html 查看属性
 */
@Configuration
@ConditionalOnProperty(name = "use.common.kafka")
public class KafkaConfiguration {

    /**
     * 生产者连接Server地址
     */
    @Value("${common.kafka.producer.bootstrap-servers}")
    private String producerBootstrapServers;

    /**
     * 生产者重试次数
     */
    @Value("${common.kafka.producer.retries:0}")
    private Integer producerRetries;

    /**
     * 批量生成信息数量大小kb
     */
    @Value("${common.kafka.producer.batch-size:8096}")
    private Integer producerBatchSize;

    /**
     * 延迟发送
     */
    @Value("${common.kafka.producer.linger-ms:5}")
    private Integer producerLingerMs;

    /**
     * 发送者批量发送的缓冲池大小
     */
    @Value("${common.kafka.producer.buffer-memory:33554432}")
    private Integer producerBufferMemory;


    /**
     * 消费组链接协同者的url
     */
    @Value("${common.kafka.consumer.bootstrap-servers}")
    private String consumerBootstrapServers;

    /**
     * 自动提交应该关闭,避免重复提交等问题
     */
    @Value("${common.kafka.consumer.enable-auto-commit:false}")
    private Boolean consumerEnableAutoCommit;

    /**
     * 自动提交间隔,关闭后无效
     */
    @Value("${common.kafka.consumer.auto-commit-interval-ms:1000}")
    private Integer consumerAutoCommitIntervalMs;

    /**
     * 默认30s
     * 超时会产生rebalance
     */
    @Value("${common.kafka.consumer.session-timeout-ms:6000}")
    private Integer consumerSessionTimeoutMs;

    /**
     * 消费组一次poll拉取最大多少条数据
     */
    @Value("${common.kafka.consumer.max-poll-records:50}")
    private Integer consumerMaxPollRecords;

    /**
     * 是否从partition最后一条记录拉取,否则会重头拉取
     */
    @Value("${common.kafka.consumer.auto-offset-reset:latest}")
    private String consumerAutoOffsetReset;

    /**
     * 心跳间隔时间 10s session time out 30s
     */
    @Value("${common.heartbeat.interval.ms:2000}")
    private Integer heartBeatIntervalMs;


    @Value("${common.kafka.poll.timeout:5000}")
    private Integer pollTimeout;

    @Value("${common.kafka.consumer.max.poll.interval.ms:5000}")
    private Integer maxPollIntervalMs;

    @Value("${common.kafka.consumer.auto.startup:true}")
    private Boolean consumerAutoStartup;

    @Value("${common.kafka.consumer.max.partition.fetch.bytes:1048576}")
    private Integer maxPartitionFetchBytes;

    /**
     * ProducerFactory
     * @return
     */
    @Primary
    @Bean
    public ProducerFactory<Object, Object> producerFactory() {
        Map<String, Object> configs = new HashMap<>(); //参数
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerBootstrapServers);
        configs.put(ProducerConfig.RETRIES_CONFIG, producerRetries);
        configs.put(ProducerConfig.BATCH_SIZE_CONFIG, producerBatchSize);
        configs.put(ProducerConfig.LINGER_MS_CONFIG, producerLingerMs);
        configs.put(ProducerConfig.BUFFER_MEMORY_CONFIG, producerBufferMemory);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        如有json的需要可以打开,但是之前的数据如果不是相同的序列化方式则会异常
//        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,JacksonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configs);
    }

    /**
     * KafkaTemplate
     * @return
     */
    @Primary
    @Bean
    public KafkaTemplate<Object, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(), true);
    }

    /**
     *
     * @return
     */
    @Primary
    @Bean
    public ConsumerFactory<Object, Object> consumerFactory() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerBootstrapServers);
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerEnableAutoCommit);

        configs.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, consumerAutoCommitIntervalMs);
        configs.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, consumerSessionTimeoutMs);
        configs.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, consumerMaxPollRecords);
        configs.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerAutoOffsetReset);
        configs.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartBeatIntervalMs);

        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes);
        //        如有json的需要可以打开,但是之前的数据如果不是相同的序列化方式则会异常
//        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,JacksonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(configs);
    }


    /**
     * 添加KafkaListenerContainerFactory
     * 用于批量消费消息
     * 在kafkaListener上需要指定containerFactory = batchContainerFactory
     * @return
     */
    @Primary
    @Bean
    public KafkaListenerContainerFactory<?> batchContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Object, Object> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        containerFactory.setConsumerFactory(consumerFactory());
        containerFactory.setConcurrency(1);
        containerFactory.setBatchListener(true);
        containerFactory.getContainerProperties().setPollTimeout(pollTimeout);
        if (!consumerEnableAutoCommit) {
            containerFactory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
        }
        containerFactory.setAutoStartup(consumerAutoStartup);
        // rebalance 监听
//        containerFactory.getContainerProperties().setConsumerRebalanceListener(new KafkaRebalanceHandler());
        return containerFactory;
    }

//    @Bean
//    public KafkaListenerContainerFactory<?> containerFactory() {
//        ContainerProperties containerProperties = new ContainerProperties("test");
//        KafkaMessageListenerContainer<Object, Object> containerFactory =
//                new KafkaMessageListenerContainer<>(consumerFactory(), containerProperties);
//        return containerFactory;
//    }

}
