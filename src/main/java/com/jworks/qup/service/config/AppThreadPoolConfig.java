package com.jworks.qup.service.config;

import com.jworks.qup.service.utils.QupServiceConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;


@Configuration
@EnableAsync
public class AppThreadPoolConfig {

    @Value("${event.multicaster.core.pool-size}")
    private int multicastPoolSize;
    @Value("${event.multicaster.max.pool-size}")
    private int multicastMaxPoolSize;
    @Value("${event.multicaster.queue.capacity}")
    private int multicastQueueCapacity;
    @Value("${async.executor.core.pool-size}")
    private int asyncCorePoolSize;
    @Value("${async.executor.max.pool-size}")
    private int asyncMaxPoolSize;
    @Value("${async.executor.queue.capacity}")
    private int asyncQueueCapacity;

    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(multicastPoolSize);
        executor.setMaxPoolSize(multicastMaxPoolSize);
        executor.setQueueCapacity(multicastQueueCapacity);
        executor.setThreadNamePrefix("QupServiceMulticastEvents-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(executor);
        return eventMulticaster;
    }

    @Bean(name = QupServiceConstants.ASYNC_EXECUTOR_BEAN_NAME)
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncCorePoolSize);
        executor.setMaxPoolSize(asyncMaxPoolSize);
        executor.setQueueCapacity(asyncQueueCapacity);
        executor.setThreadNamePrefix("QupServiceAsyncTask-");
        executor.initialize();

        return executor;
    }


}
