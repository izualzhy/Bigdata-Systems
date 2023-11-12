package cn.izualzhy;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

@Slf4j
public class HelloJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        context.getJobDetail().getJobDataMap().forEach((k, v) ->
                log.info("param {} -> {}", k, v));

        log.info("Hello Job now:" + new Date());
    }
}
