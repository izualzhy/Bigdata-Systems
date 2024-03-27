package cn.izualzhy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MapComparator {
    private static final Logger logger = LoggerFactory.getLogger(WatchSample.class);


    public static boolean compareMaps(Map<String, String> map1, Map<String, String> map2) {
        if (map1 == null && map2 == null) {
            logger.info("all null");
            return true;
        }
        if (map1 == null || map2 == null) {
            logger.info("either null");
            return false;
        }
        // Check if both maps have the same size
        if (map1.size() != map2.size()) {
            logger.info("size differs");
            return false;
        }

        // Check if all keys and values are equal
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            String key = entry.getKey();
            String value1 = entry.getValue();
            String value2 = map2.get(key);

            // If the value for a key is different or the key doesn't exist in the second map
            if (value2 == null || !value1.equals(value2)) {
                logger.info("value differs");
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        Map<String, String> map1 = new HashMap<>();
        map1.put("key1", "value1");
        map1.put("key2", "value2");
        map1.put("key3", "value3");

        Map<String, String> map2 = new HashMap<>();
        map2.put("key1", "value1");
        map2.put("key2", "value2");
        map2.put("key3", "value3");

        boolean isEqual = compareMaps(map1, map2);
        if (isEqual) {
            System.out.println("Maps are identical.");
        } else {
            System.out.println("Maps are different.");
        }
    }
}