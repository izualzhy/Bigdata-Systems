package cn.izualzhy;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class JobStartupRunner implements CommandLineRunner {
    @Autowired
    JobStartupConfig jobStartupConfig;
    private static final String TRIGGER_GROUP_NAME = "job_startup_runner_test_trigger";
    private static final String JOB_GROUP_NAME = "job_startup_runner_job_group_name";

    @Override
    public void run(String... args) throws Exception {
        Scheduler scheduler;
        try {
            scheduler = jobStartupConfig.getScheduler();
            addJob(scheduler, 1);
            addJob(scheduler, 2);
        } catch (Exception e) {
            System.out.println("e.message:" + e.getMessage());
        }
    }

    private void addJob(Scheduler scheduler, Integer index) throws SchedulerException {
        String triggerName = "trigger-" + index;
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, TRIGGER_GROUP_NAME);
        Trigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

        if (trigger == null) {
            JobDetail jobDetail = JobBuilder.newJob(QuartzJob.class)
                    .withIdentity("job-" + index, JOB_GROUP_NAME)
                    .usingJobData("name", "quartz-job" + index)
                    .build();
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("* */3 * * * ?");
            trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerName, TRIGGER_GROUP_NAME)
                    .withSchedule(cronScheduleBuilder)
                    .build();
            scheduler.scheduleJob(jobDetail, trigger);
            System.out.println("create quartz job = " + jobDetail.getKey());
        } else {
            System.out.println("exist quartz job = " + trigger.getKey());
        }
    }
}
