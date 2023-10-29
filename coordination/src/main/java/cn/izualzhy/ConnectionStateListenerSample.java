package cn.izualzhy;

import javafx.scene.input.DataFormat;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.Date;

public class ConnectionStateListenerSample {
    public static void sample(String connectionString) {
        try (CuratorFramework client = Util.createWithOptions(connectionString,
                new ExponentialBackoffRetry(1000, 3),
                10000,
                90000)) {
//            try (CuratorFramework client = Util.createSimple(connectionString)) {
                client.getConnectionStateListenable().addListener((c, connectionState) -> {
                    System.out.println("c = " + c + " d = " + new Date());
                    System.out.println(connectionState);
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
