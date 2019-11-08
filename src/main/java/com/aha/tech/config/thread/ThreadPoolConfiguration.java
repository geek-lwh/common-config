package com.aha.tech.config.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @Author: luweihong
 * @Date: 2018/8/8
 */
@Configuration
@ConditionalOnProperty(name = "use.common.core.thread", matchIfMissing = true)
public class ThreadPoolConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolConfiguration.class);

    @Value("${common.core.thread.pool.size:100}")
    private int corePoolSize;

    @Value("${common.core.thread.max.pool.size:200}")
    private int maxPoolSize;

    @Value("${common.core.thread.queue.capacity.size:1000}")
    private int queueCapacity;

    @Value("${common.core.thread.name.prefix:core-thread}")
    private String threadNamePrefix;

    @Bean("coreThreadPool")
    public ThreadPoolTaskExecutor coreThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        logger.info("core thread pool init finish >> {}", executor);
        return executor;
    }

    @Bean("nonCoreThreadPool")
    public ThreadPoolTaskExecutor nonCoreThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(40);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("non-core-thread-pool");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        logger.info("non core thread pool init finish >> {}", executor);
        return executor;
    }

}
