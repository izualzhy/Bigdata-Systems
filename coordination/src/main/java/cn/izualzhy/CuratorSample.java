package cn.izualzhy;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.zookeeper.CreateMode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * <a href="https://curator.apache.org/docs/getting-started">...</a>
 */
public class CuratorSample {

    public static void main(String[] args) throws InterruptedException {
        lock(args[0]);
    }

    public static void lock(String connectionString) throws InterruptedException {
        String lockPath = "/sample/lock/lock_test";

        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < 10; ++i) {
            threads.add(
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try (CuratorFramework client = Util.createSimple(connectionString)) {
                                client.start();
                                InterProcessMutex mutex = new InterProcessMutex(client, lockPath);
                                if (mutex.acquire(600, TimeUnit.SECONDS)) {
                                    try {
                                        System.out.println(Thread.currentThread() + " acquire lock for path:" + lockPath);
                                        Thread.sleep(60000);
                                    } finally {
                                        System.out.println(Thread.currentThread() + " release lock for path:" + lockPath);
                                        mutex.release();
                                    }
                                } else {
                                    System.out.println(Thread.currentThread() + " giveup lock for path:" + lockPath);
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    })
            );
        }

        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }

    public static void test(String connectionString) throws Exception {
        try (CuratorFramework client = Util.createSimple(connectionString)) {
            System.out.println(client);
            System.out.println(client.getState());
            client.start();
            System.out.println(client.getState());

            System.out.println("create persistent");
            client.create()
                    .creatingParentsIfNeeded()
                    .forPath("/sample/test_key", "test_value".getBytes(StandardCharsets.UTF_8));

            System.out.println("create ephemeral");
            client.create()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath("/sample/test_ephemeral_key", "test_ephemeral_value".getBytes(StandardCharsets.UTF_8));

            System.out.println("create ephemeral seq");
            for (int i = 0; i < 5; i++) {
                client.create()
                        .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                        .forPath("/sample/test_ephemeral_seq_key", "test_ephemeral_seq_value".getBytes(StandardCharsets.UTF_8));
            }

            System.out.println("set data");
            client.setData().forPath("/sample/test_key", "test_value_v2".getBytes(StandardCharsets.UTF_8));

            System.out.println("set data async");
            CuratorListener listener = new CuratorListener() {
                @Override
                public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
                    System.out.println("name:" + event.getName());
                    System.out.println("data:" + new String(event.getData()));
                    System.out.println("stat:" + event.getStat());
                }
            };
            client.getCuratorListenable().addListener(listener);
            client.setData().inBackground().forPath("/sample/test_key", "test_value_v3".getBytes(StandardCharsets.UTF_8));

            System.out.println("sleep");
            Thread.sleep(60000);
            System.out.println("exit");
        }
    }
}

