package cn.izualzhy;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HelloConfig {
    @Bean("")
    public JobDetail helloJobDetail() {
        return JobBuilder.newJob(HelloJob.class)
                .withIdentity("hello_job", "hello_group")
                .usingJobData("name", "hello")
                .usingJobData("some_key", "some_value")
                .withDescription("hello world test.")
                .build();
    }

    @Bean
    public Trigger helloJobTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("* */5 * * * ?");
        return TriggerBuilder.newTrigger()
                .forJob(helloJobDetail())
                .withIdentity("hello_trigger", "hello_group")
                .withSchedule(cronScheduleBuilder)
                .build();
    }
}
