package cn.izualzhy;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorTest {
    public static void addTask(ExecutorService executorService, Integer i) {
        Future<Integer> future = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                System.out.println(Thread.currentThread());
                System.out.println(String.format("thread-%d", Thread.currentThread().getId()));
                int sleepSeconds = new Random().nextInt(1000);
                Thread.sleep(sleepSeconds + 30000);
                if (i >= 0) {
                    return sleepSeconds;
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
        ExecutorService executorService = Executors.newSingleThreadExecutor();
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
