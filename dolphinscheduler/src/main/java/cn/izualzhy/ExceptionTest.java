package cn.izualzhy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionTest {

    static void foo() {
        try {
            int a = 1 / 0;
        } catch (java.lang.ArithmeticException e) {
//            System.out.println(e);
            throw new RuntimeException("foo", e);
//            throw e;
        }
    }

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(ExceptionTest.class);

        try {
            foo();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getMessage());
            System.out.println(String.valueOf(e.getCause()));
            System.out.println(e);
            System.out.println(e.toString());
            System.out.println(e.getClass().getName());
            System.out.println("------");
            e.printStackTrace();
//            System.out.println("SqlTask handle error", e);
        }
    }
}
