package cn.izualzhy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class ExceptionTest {
    static final Logger logger = LoggerFactory.getLogger(ExceptionTest.class);

    static void foo() {
        try {
            int a = 1 / 0;
        } catch (java.lang.ArithmeticException e) {
//            System.out.println(e);
            throw new RuntimeException("foo", e);
//            throw e;
        }
    }

    static void foo2() throws SQLException {
        try {
            foo();
        } catch (Exception e) {
            throw new SQLException("sql_exception_reason", "08S01", e);
        }
    }

    static void testSoftKill() {
//        while (true) {
//            try {
//                Thread.sleep(10000);
//            } catch (Exception e) {
//                System.out.println(e);
//                e.printStackTrace();
//            }
//        }
    }

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(ExceptionTest.class);
//        testSoftKill();

        int i = 0;
        try {
            while (true) {
                Thread.sleep(1000);
                ++i;

                System.out.println(i);
                if (i >= 10) {
                    System.out.println(i);
                    throw new RuntimeException(String.format("i(%d)-execpetion", i));
                }
            }
//            foo2();
        } catch (Exception e) {
            logger.info("exception string:{}", e.toString());
            logger.info("exception:", e);
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getMessage());
            System.out.println(String.valueOf(e.getCause()));
            System.out.println(e);
            System.out.println(e.toString());
            System.out.println(e.getClass().getName());
            System.out.println("------");
            e.printStackTrace();
//            System.out.println("SqlTask handle error", e);
        } finally {
            System.out.println("in finally.");
        }
    }
}
