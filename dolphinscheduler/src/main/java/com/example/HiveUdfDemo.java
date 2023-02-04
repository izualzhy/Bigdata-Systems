package com.example;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;

import java.util.HashMap;
import java.util.Map;

@Description(
        name = "season_year",
        value = "_FUNC_(season, year) calculate the year season",
        extended = "Example: SELECT _FUNC_(1, 2016) returns 2018@春"

)
public class HiveUdfDemo extends UDF {
    public static Map<Integer, String> seasonMap = new HashMap<>();
    static {
        seasonMap.put(1, "春");
        seasonMap.put(2, "夏");
        seasonMap.put(3, "秋");
        seasonMap.put(31, "秋_1");
        seasonMap.put(4, "冬");
    }

    public String evaluate(int season, int year) {
        String seasonName =  seasonMap.getOrDefault(season, "UNKNOWN");
        return String.format("%d@%s", year, seasonName);
    }
}
