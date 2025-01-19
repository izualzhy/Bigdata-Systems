package cn.izualzhy;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

public class ConcurrentSetSample {
    private static final int THREAD_NUM = 32;
    public static void main(String[] args) throws Exception {
        ConcurrentSetSample concurrentSetSample = new ConcurrentSetSample();
//        concurrentSetSample.testConcurrentHashMapSet();
        concurrentSetSample.benchmarkConcurrentAndCopyOnWriteSet();
    }

    void testConcurrentHashMapSet() throws InterruptedException {
        Set<String> concurrentSet = ConcurrentHashMap.newKeySet();
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUM);

        // 提交写任务
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                for (int j = 0; j <= 10000; j++) {
                    String element = "Element-" + j;
                    concurrentSet.add(element);
                }
            });
        }

        for (int i = 0; i < 10; i++) {
            System.out.println(String.format("concurrentSet size: %d , Element-1 in set : %b, Element-10000 in set : %b",
                    concurrentSet.size(),
                    concurrentSet.contains("Element-1"),
                    concurrentSet.contains("Element-10000")));
        }

        // 关闭线程池并等待所有任务完成
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

    }

    void benchmarkConcurrentAndCopyOnWriteSet() throws InterruptedException {
        int writeThreads = 0;
        Set<String> concurrentSet = ConcurrentHashMap.newKeySet();
        System.out.println("Test On ConcurrentHashMap.newKeySet:");
        testReadMoreWriteLessScene(concurrentSet, THREAD_NUM - writeThreads, writeThreads);

        CopyOnWriteArraySet<String> copyOnWriteSet = new CopyOnWriteArraySet<>();
        System.out.println("Test On CopyOnWriteArraySet:");
        testReadMoreWriteLessScene(copyOnWriteSet, THREAD_NUM - writeThreads, writeThreads);

        HashSet<String> hashSet = new HashSet<>();
        System.out.println("Test On HashSet:");
        testReadMoreWriteLessScene(hashSet, THREAD_NUM - writeThreads, writeThreads);
    }

    void testReadMoreWriteLessScene(Set<String> set, int readThreads, int writeThreads) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(readThreads + writeThreads);

        long startTime = System.currentTimeMillis();
        // 提交读任务
        for (int i = 0; i < readThreads; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < 10000; j++) { // 每个线程执行多次读取
                    for (String element : set) {
                        // 模拟读操作
                        element.hashCode(); // 防止优化忽略遍历
                    }
                }
//                System.out.println(Thread.currentThread().getName() + " finished reading.");
            });
        }

        // 提交写任务
        for (int i = 0; i < writeThreads; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < 10000; j++) { // 每个线程执行少量写操作
                    String element = "NewElement-" + j + "-" + Thread.currentThread().getName();
                    set.add(element);
                }
            });
        }

        // 关闭线程池并等待任务完成
        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);

        // 结束计时
        long endTime = System.currentTimeMillis();

        System.out.println("Read-Write-Less Scene: " + (endTime - startTime) + " ms");
    }
}
