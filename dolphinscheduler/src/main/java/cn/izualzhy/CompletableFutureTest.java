package cn.izualzhy;

import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class CompletableFutureTest {
    private static Random rnd = new Random();
    static int delayRandom(int min, int max) {
        int milli = max > min ? rnd.nextInt(max - min) : 0;
        try {
            Thread.sleep(min + milli);
            System.out.println("after sleep "  + (min + milli) + " ms.");
        } catch (InterruptedException e) {
        }

        return min + milli;
    }


    private static ExecutorService  executorService = Executors.newFixedThreadPool(10);

    static Future<Integer> callExternalServiceUseFuture() {
        Callable<Integer> externalTask = () -> {
            int time = delayRandom(20, 2000);
            return time;
        };

        return executorService.submit(externalTask);
    }

    static Future<Integer> callExternalServiceUseCompletableFuture() {
        Supplier<Integer> externalTask = () -> {
            int time = delayRandom(20, 2000);
            return time;
        };

        return CompletableFuture.supplyAsync(externalTask, executorService);
    }

    static void master() {
        Future<Integer> asyncRet = callExternalServiceUseFuture();
        try {
            Integer ret = asyncRet.get();
            System.out.println(ret);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
    }

    public static void main(String[] args) {
        master();
    }
}
