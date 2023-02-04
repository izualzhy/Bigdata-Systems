package cn.izualzhy;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Predicate;

public class ExecutorServiceTest {
    public static void testFixedThreadPool() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        ArrayList<Future> futures = new ArrayList<>();
        for (int i = 0; i < 15; ++i) {
            futures.add(executorService.submit(new Runnable() {
                @Override
                public void run() {
                    int sleepMs = new Random().nextInt(10000) + 30000;
                    System.out.println(Thread.currentThread() + " going to sleep " + String.valueOf(sleepMs) + " ms.");

                    try {
                        Thread.sleep(sleepMs);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }));
        }

        System.out.println(String.format("%d futures waiting", futures.size()));
        ThreadPoolExecutor threadPoolExecutor = ((ThreadPoolExecutor)executorService);
        threadPoolExecutor.getPoolSize();

        for (int i = 0; i < 60; i ++) {
            System.out.println(String.format("%d %d %d %d %d %d %d",
                    threadPoolExecutor.getPoolSize(), threadPoolExecutor.getCorePoolSize(), threadPoolExecutor.getMaximumPoolSize(),
                    threadPoolExecutor.getActiveCount(), threadPoolExecutor.getTaskCount(), threadPoolExecutor.getCompletedTaskCount(),
                    threadPoolExecutor.getQueue().size()));
            boolean allFinished = futures.stream().allMatch(new Predicate<Future>() {
                @Override
                public boolean test(Future future) {
                    return future.isDone();
                }
            });
            if (allFinished) {
                System.out.println("all success.");
                break;
            }

            try {
                Thread.sleep(5000);
            } catch (Exception e) {
            }
        }

        executorService.shutdown();
    }

    public static void main(String[] args) {
        testFixedThreadPool();
    }
}
