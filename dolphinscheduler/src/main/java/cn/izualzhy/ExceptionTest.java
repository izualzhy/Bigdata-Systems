package cn.izualzhy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionTest {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(ExceptionTest.class);

        try {
            int a = 1 / 0;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            logger.error(e.getMessage());
            logger.error(String.valueOf(e.getCause()));
            logger.error(e.toString());
            logger.error(e.getClass().getName());
            logger.error("SqlTask handle error", e);
        }
    }
}
