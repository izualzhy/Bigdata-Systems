package cn.izualzhy.springmore.service.impl;

import cn.izualzhy.springmore.service.AsyncService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncServiceImpl implements AsyncService {
    @Async
    @Override
    public void generateReport() throws InterruptedException {
        Thread.sleep(5000);
        System.out.println("generateReport in thread:" + Thread.currentThread().getName());
    }
}
