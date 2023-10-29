package cn.izualzhy;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;

public class TreeCacheSample {
    public static void foo(String connectionString) {
        try (CuratorFramework client = Util.createSimple(connectionString)) {
            client.start();
            TreeCache treeCache = new TreeCache(client, "/sample");

            treeCache.getListenable().addListener((c, event) -> {
                System.out.println("\n c = " + c + " thread = " + Thread.currentThread());
                System.out.println("event = " + event + " type = " + event.getType());
                System.out.println("oldData = " + event.getOldData() + " data = " + event.getData());
            });
            treeCache.start();

            Thread.sleep(600000);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {}
    }

    public static void main(String[] args) {
        foo(args[0]);
    }
}
