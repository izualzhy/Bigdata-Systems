package cn.izualzhy;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DaemonThreadInJVM {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("main start");

        registerGracefulExitHandler();

        long startTime =  System.nanoTime();
        Thread t = new Thread(() -> {
            try {
                TimeUnit.MINUTES.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                System.out.println("error:" + e);
            }
        });
        t.setName("daemon thread run long-long-long");
        t.setDaemon(true);
        t.start();

        TimeUnit.SECONDS.sleep(15);

        for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            System.out.println("thread:" + entry.getKey());
            for (StackTraceElement traceElement : entry.getValue()) {
                System.out.println("\t" + traceElement);
            }
            System.out.println("\n");
        }

        System.out.println("main exit, elapsed " + (System.nanoTime() - startTime) / 1000 / 1000/ 1000 + " seconds");
    }

    static void registerGracefulExitHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("do some clean here.");
        }));
    }
}
