package cn.izualzhy;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.concurrent.Executor;

@Configuration
public class JobStartupConfig {
    @Autowired
    private DataSource dataSource;

    @Bean
    Scheduler getScheduler() {
        return schedulerFactoryBean().getScheduler();
    }

    @Bean
    SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setSchedulerName("Cluster_Scheduler");
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationConext");
        schedulerFactoryBean.setTaskExecutor(schedulerExecutor());
        schedulerFactoryBean.setStartupDelay(10);

        return schedulerFactoryBean;
    }

    @Bean
    Executor schedulerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setQueueCapacity(Runtime.getRuntime().availableProcessors());
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());

        return executor;
    }

}
