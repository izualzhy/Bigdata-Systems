package cn.izualzhy;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class TestClass {
    int a = 1;
    final String[] values;

    static final HashMap<String, String> hashMap = new LinkedHashMap<>();

    TestClass() {
        values = new String[6];
        values[0] = "hello";
        values[1] = "world";
        values[2] = "a";
        values[3] = "b";
        values[4] = "c";
        values[5] = "d";
    }
}
