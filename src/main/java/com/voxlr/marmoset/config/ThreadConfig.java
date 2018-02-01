package com.voxlr.marmoset.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class ThreadConfig {

    @Bean(name = "transcribeExecutor")
    public TaskExecutor transcribeExecutor() {
	ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	executor.setCorePoolSize(3);
	executor.setMaxPoolSize(5);
	executor.setThreadNamePrefix("transcribe_task_executor_thread");
	executor.initialize();
	
	return executor;
    }
    
    @Bean(name = "analysisExecutor")
    public TaskExecutor analysisExecutor() {
	ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	executor.setCorePoolSize(3);
	executor.setMaxPoolSize(8);
	executor.setThreadNamePrefix("transcribe_task_executor_thread");
	executor.initialize();
	
	return executor;
    }
}
