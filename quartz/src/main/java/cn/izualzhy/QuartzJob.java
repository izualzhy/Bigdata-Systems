package cn.izualzhy;

import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Slf4j
public class QuartzJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) {
        String taskName = context.getJobDetail().getJobDataMap().getString("name");
        log.info("start taskName:{} id:{}\n\t     fire-time:{}\n\tnext-fire-time:{}\n\tsche-fire-time|{}",
                taskName,
                context.getFireInstanceId(),
                context.getFireTime(),
                context.getNextFireTime(),
                context.getScheduledFireTime());
        try {
            Thread.sleep(15000);
        } catch (Exception e) {
        }

        log.info("stop  taskName:{}", taskName);
    }
}
