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
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String taskName = context.getJobDetail().getJobDataMap().getString("name");
        log.info("quartz job taskName:{} now:{} thread:{}", taskName, new Date(), Thread.currentThread());
    }
}
