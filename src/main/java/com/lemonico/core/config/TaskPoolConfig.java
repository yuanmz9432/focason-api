package com.lemonico.core.config;



import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @className: TaskPoolConfig
 * @description: 定义异步线程池
 * @date: 2021/8/11 14:55
 **/
@Configuration
public class TaskPoolConfig
{

    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数为10
        executor.setCorePoolSize(10);

        // 最大线程数为20
        executor.setMaxPoolSize(20);

        // 缓冲队列200
        executor.setQueueCapacity(200);

        // 允许线程空闲时间为60s
        executor.setKeepAliveSeconds(60);

        // 线程名前缀
        executor.setThreadNamePrefix("taskExecutor--");

        // 会让被线程池拒绝的任务直接抛弃，不会抛异常也不会执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

        // 设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住
        executor.setAwaitTerminationSeconds(300);

        return executor;
    }

}
