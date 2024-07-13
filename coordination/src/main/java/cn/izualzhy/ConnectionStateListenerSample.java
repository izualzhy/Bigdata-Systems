package cn.izualzhy;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.Date;

public class ConnectionStateListenerSample {
    public static void sample(String connectionString) {
        try (CuratorFramework client = Util.createWithOptions(connectionString,
                new ExponentialBackoffRetry(1000, 1),
                10000,
                90000)) {
                client.getConnectionStateListenable().addListener((c, connectionState) -> {
                    System.out.println("c = " + c + " d = " + new Date());
                    System.out.println("connectionState = " + connectionState);
                });

                client.start();
                System.out.println("client = " + client + " d = " + new Date());

                Thread.sleep(300000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
            }
    }

    public static void main(String[] args) {
        sample(args[0]);
    }
}
