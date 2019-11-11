package com.aha.tech.config.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @Author: luweihong
 * @Date: 2019/11/8
 */
@Configuration
@ConditionalOnProperty(name = "use.common.task.thread", matchIfMissing = true)
public class TaskThreadPoolConfiguration {

    private Logger logger = LoggerFactory.getLogger(TaskThreadPoolConfiguration.class);

    @Value("${common.task.pool.size:10}")
    private Integer poolSize;

    /**
     * 专门用于执行定时任务的线程池
     * 只要有@scheduler标签则会使用该线程池
     * @return
     */
    @Primary
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(poolSize);
        taskScheduler.setRemoveOnCancelPolicy(true);
        taskScheduler.setErrorHandler(t -> logger.error("Unexpected error occurred in scheduled task.", t));
        taskScheduler.initialize();
        logger.info("task thread pool init finish >> {}", taskScheduler);

        return taskScheduler;
    }
}
