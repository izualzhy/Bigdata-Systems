package cn.izualzhy;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class JustTest {
    static String foo(Object b) {
        System.out.println("-----------");
        System.out.println(b);

        String value;
        if (null == b) {
            value = "NULL";
        } else if (b instanceof byte[]) {
//            System.out.println("byte[]");
            value = new String((byte[])b);
        } else if (b instanceof Number) {
            System.out.println(b.getClass() + " is Number");
            value = new BigDecimal(((Number) b).toString()).toPlainString();
//            value = ((BigDecimal) b).toPlainString();
        } else {
            value = b.toString();
        }

        return value;
    }

    public static void main(String[] args) {
        TestClass testClass1 = new TestClass();
        testClass1.hashMap.put("testClass1", "hello");
        for (Map.Entry<String, String> entry : testClass1.hashMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        TestClass testClass2 = new TestClass();
        testClass2.hashMap.put("testClass2", "hello");
        for (Map.Entry<String, String> entry : testClass2.hashMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date(1677942000000L)));
    }
}
