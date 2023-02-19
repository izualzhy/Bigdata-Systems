package cn.izualzhy;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class JustTest {
    public static void main(String[] args) {
        Properties properties = System.getProperties();
        System.out.println(properties);

        for (Map.Entry entry: properties.entrySet()){
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }

        System.out.println(Long.getLong("java.class.version"));
        System.out.println(Long.getLong("os.version"));
        System.out.println(Long.getLong("a.b.c"));

        System.setProperty("a.b.c", "123456");
        System.out.println(Long.getLong("a.b.c"));

        System.out.println(System.getProperty("java.class.version"));
        System.out.println(System.getProperty("a.b.c"));

        String v = System.getProperty("java.class.version");
        if (v != null) {
            try {
                System.out.println(Long.decode(v));
            } catch (NumberFormatException e) {
                System.out.println(e);
            }
        }
        System.out.println(Long.getLong("sun.arch.data.model"));
    }
}
