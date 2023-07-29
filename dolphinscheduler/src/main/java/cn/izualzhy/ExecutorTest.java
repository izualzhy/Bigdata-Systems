package cn.izualzhy;

import com.sun.jna.platform.win32.Kernel32;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;



@Slf4j
public class ExecutorTest {
    public static void addTask(ExecutorService executorService, Integer i) {
        Future<Integer> future = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int sleepMillis = new Random().nextInt(10000);
                System.out.println("i:" + i  + " sleep " + sleepMillis + " " + Thread.currentThread() + " thread-" + Thread.currentThread().getId());
                log.info("i:" + i
                        + " sleep " + sleepMillis
                        + " " + Thread.currentThread()
                        + " thread-" + Thread.currentThread().getId()
                        + " processId:" + Kernel32.INSTANCE.GetCurrentProcessId());
                Thread.sleep(sleepMillis + 30000);
                if (i >= 0) {
                    return sleepMillis;
                } else {
                    throw new RuntimeException("hello world");
                }
            }
        });

        try {
            System.out.println(future.get());
        } catch (Exception e) {
            System.out.println("Exception:");
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (int i = -5; i < 10; i++) {
            addTask(executorService, i);
        }

//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                throw new RuntimeException("hello world");
//            }
//        });
//        t.start();

//        System.out.println("main exit.");
//        executorService.shutdown();
    }
}
