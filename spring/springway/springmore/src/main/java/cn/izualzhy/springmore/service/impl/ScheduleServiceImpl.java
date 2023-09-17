package cn.izualzhy.springmore.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduleServiceImpl {
    int count1 = 1;
    int count2 = 1;
    int count3 = 1;
    int count4 = 1;
    @Async
    @Scheduled(fixedRate = 1000)
    void job1() {
        System.out.println("job1 fixedRate:1000 thread:" + Thread.currentThread() + " count1:" + count1);
        ++count1;
    }

    @Async
    @Scheduled(fixedRate = 5000)
    void job2() {
        System.out.println("job2 fixedRate:5000 thread:" + Thread.currentThread() + " count2:" + count2);
        ++count2;
    }
}
