package cn.izualzhy;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class GuavaCacheSample {
    public static void main(String[] args) {
        test();
        testMaximumSize();
        testTTL();
    }

    private static void test() {
        CacheBuilder cacheBuilder = CacheBuilder.newBuilder();
        Cache cache = cacheBuilder.build();

        cache.put("cache_key", "cache_value");

        String value = (String) cache.getIfPresent("cache_key");
        System.out.println(value);

        cache.invalidate("cache_key");
        value = (String) cache.getIfPresent("cache_key");
        System.out.println(value);
    }

    private static void testMaximumSize() {
        // test maximumSize
        CacheBuilder cacheBuilder = CacheBuilder.newBuilder().maximumSize(10);
        Cache cache = cacheBuilder.build();
        for (int i = 0; i < 10; i++) {
            cache.put("cache_key_" + i, "cache_value_" + i);
        }
        System.out.println("cache 10 elems:");
        for (int i = 0; i < 10; i++) {
            System.out.println(cache.getIfPresent("cache_key_" + i));
        }
        System.out.println("cache 11 elems:");
        cache.put("cache_key_" + 10, "cache_value_" + 10);
        for (int i = 0; i < 10; i++) {
            System.out.println(cache.getIfPresent("cache_key_" + i));
        }
    }

    private static void testTTL() {
        // test TTL
        CacheBuilder cacheBuilder = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(2, TimeUnit.MINUTES);
        Cache cache = cacheBuilder.build();
        cache.put("cache_key", "cache_value");
        System.out.println();

        int tryTimes = 0;
        while (true) {
            String value = (String) cache.getIfPresent("cache_key");
            System.out.println("tryTimes:" + tryTimes + " value:" + value);
            tryTimes += 1;
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
            }
        }
    }
}
