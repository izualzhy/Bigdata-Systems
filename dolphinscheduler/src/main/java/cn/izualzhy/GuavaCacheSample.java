package cn.izualzhy;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GuavaCacheSample {
    static final Cache<String, Integer> testCache = CacheBuilder.newBuilder()
            .maximumSize(30000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    public void fillCache() {
        testCache.put("hello", 1);
    }
    public Integer getCache(String key) {
        return testCache.getIfPresent(key);
    }

    public static void main(String[] args) throws ExecutionException {
//        test();
//        testMaximumSize();
//        testTTL();
//        testRefreshAfterWriter();
        GuavaCacheSample guavaCacheSampleA = new GuavaCacheSample();
        guavaCacheSampleA.fillCache();
        GuavaCacheSample guavaCacheSampleB = new GuavaCacheSample();
        System.out.println(guavaCacheSampleB.getCache("hello"));
    }


    private static void test() throws ExecutionException {
        CacheBuilder cacheBuilder = CacheBuilder.newBuilder();
        Cache cache = cacheBuilder.build();

        cache.put("cache_key", "cache_value");

        String value = (String) cache.getIfPresent("cache_key");
        System.out.println(value);

        cache.invalidate("cache_key");
        value = (String) cache.getIfPresent("cache_key");
        System.out.println(value);

        value = (String) cache.get("cache_key", () -> {
            return "cache_value";
        });
        System.out.println(value);
        value = (String) cache.getIfPresent("cache_key");
        System.out.println(value);

        cache.put("cache_key", null);
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

    private static void testExpireAfterWrite() {
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
    private static void testRefreshAfterWriter() throws ExecutionException {
        LoadingCache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .refreshAfterWrite(1, TimeUnit.MINUTES)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String s) throws Exception {
                        return "cache_value_" + System.currentTimeMillis() / 1000;
                    }
                });
        int tryTimes = 0;
        while (true) {
            String value = (String) cache.get("cache_key");

            System.out.println("tryTimes:" + tryTimes + " value:" + value);
            tryTimes += 1;
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
            }
        }

    }
}
