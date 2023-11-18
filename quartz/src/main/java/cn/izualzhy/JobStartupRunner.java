package cn.izualzhy;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class JobStartupRunner implements CommandLineRunner {
    JobStartupConfig jobStartupConfig;
    Scheduler scheduler;
    private static final String TRIGGER_GROUP_NAME = "job_startup_runner_test_trigger";
    private static final String JOB_GROUP_NAME = "job_startup_runner_job_group_name";

    JobStartupRunner(JobStartupConfig jobStartupConfig, SchedulerFactoryBean schedulerFactoryBean) {
        this.jobStartupConfig = jobStartupConfig;
        this.scheduler = schedulerFactoryBean.getScheduler();
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            addJob(scheduler, 1, true);
//            addJob(scheduler, 2, true);
        } catch (Exception e) {
            System.out.println("e.message:" + e.getMessage());
        }
    }

    private void addJob(Scheduler scheduler, Integer index, boolean overwrite) throws SchedulerException {
        String triggerName = "trigger-" + index;
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, TRIGGER_GROUP_NAME);
        Trigger trigger = scheduler.getTrigger(triggerKey);

        if (trigger != null) {
            System.out.println("exist quartz job = " + trigger.getKey());
            if (overwrite) {
                scheduler.deleteJob(trigger.getJobKey());
                System.out.println("delete quartz job = " + trigger.getKey());
                trigger = null;
            }
        }

        if (trigger == null) {
            JobDetail jobDetail = JobBuilder.newJob(QuartzJob.class)
                    .withIdentity("job-" + index, JOB_GROUP_NAME)
                    .usingJobData("name", "quartz-job" + index)
                    .build();
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("*/10 * * * * ?")
                    .withMisfireHandlingInstructionDoNothing();
            trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerName, TRIGGER_GROUP_NAME)
                    .withSchedule(cronScheduleBuilder)
                    .build();
            scheduler.scheduleJob(jobDetail, trigger);
            System.out.println("create quartz job = " + jobDetail.getKey());
        }
    }
}
