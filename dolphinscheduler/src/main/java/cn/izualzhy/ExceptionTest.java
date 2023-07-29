package cn.izualzhy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionTest {

    static void foo() {
        try {
//            int a = 1 / 0;
        } catch (java.lang.ArithmeticException e) {
//            System.out.println(e);
            throw new RuntimeException("foo", e);
//            throw e;
        }
    }

    static void testSoftKill() {
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(ExceptionTest.class);
        testSoftKill();

//        try {
//            foo();
//        } catch (Exception e) {
//            logger.info("exception:{}", e.toString());
//            System.out.println(e.getLocalizedMessage());
//            System.out.println(e.getMessage());
//            System.out.println(String.valueOf(e.getCause()));
//            System.out.println(e);
//            System.out.println(e.toString());
//            System.out.println(e.getClass().getName());
//            System.out.println("------");
//            e.printStackTrace();
////            System.out.println("SqlTask handle error", e);
//        }
    }
}
