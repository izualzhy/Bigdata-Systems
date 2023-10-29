package cn.izualzhy;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.ZKPaths;

public class PathChildrenCacheSample {
    public static void test(String connectString) {
        try (CuratorFramework client = Util.createSimple(connectString)) {
            client.start();

            PathChildrenCache pathChildrenCache = new PathChildrenCache(client, "/sample/test_path", true);
            pathChildrenCache.start();

            pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    switch (event.getType()) {
                        case CHILD_ADDED:
                            System.out.println("Node added: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                            break;
                        case CHILD_UPDATED:
                            System.out.println("Node updated: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                            break;
                        case CHILD_REMOVED:
                            System.out.println("Node removed: " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                            break;
//                        case CONNECTION_LOST:
//                            System.out.println("connection lost");
//                            break;
//                        case CONNECTION_RECONNECTED:
//                            System.out.println("connection reconnected");
//                            break;
//                        case CONNECTION_SUSPENDED:
//                            System.out.println("connection suspended");
//                            break;
                        default:
                            System.out.println("unknown type = " + event.getType());
                    }
                }
            });

            Thread.sleep(1000001);
        } catch (Exception e) {
            System.out.println("e:" + e);
        } finally {

        }
    }

    public static void main(String[] args) {
        test(args[0]);
    }
}
