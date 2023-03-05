package cn.izualzhy;

import java.io.File;
import java.math.BigDecimal;
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
//        Object b = BigDecimal.valueOf(6651968276123456L);
        Object b = 528727263.59;
        System.out.println(foo(b));

        b = 1;
        System.out.println(foo(b));

        b = 'c';
        System.out.println(foo(b));

        b = "hello";
        System.out.println(foo(b));

        b = "1.23";
        System.out.println(foo(b));

        b = 1.23;
        System.out.println(foo(b));

        b = 123;
        System.out.println(foo(b));

        b = 123456789101678910L;
        System.out.println(foo(b));

        b = new Float(1.23456789);
        System.out.println(foo(b));

        b = new Double(123);
        System.out.println(foo(b));
    }
}
