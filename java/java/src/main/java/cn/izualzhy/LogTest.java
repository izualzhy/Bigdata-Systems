package cn.izualzhy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.PrintStream;

public class LogTest {
    private Logger logger = LoggerFactory.getLogger(LogTest.class);
    void logTest() {
        logger.info("this is a info log.");
        System.out.println("this is a stdout log A");
//        redirectSystemOutToLog();
        System.out.println("this is a stdout log B");

        try {
            throwException();
        } catch (Exception e) {
            logger.info("throwException", e);
        }
    }

    void throwException() {
        String s = null;
        s.length();
//        throw new RuntimeException("test");
    }

    public static void redirectSystemOutToLog() {
        System.setOut(new PrintStream(new OutputStream() {
            private final Logger logger = LoggerFactory.getLogger("SystemOutLogger");
            private final StringBuilder buffer = new StringBuilder();

            @Override
            public void write(int b) {
                char c = (char) b;
                String value = Character.toString(c);
                if (value.equals("\n")) {
                    logger.info(buffer.toString());
                    buffer.setLength(0); // Clear the buffer
                } else {
                    buffer.append(c);
                }
            }
        }));
    }

    public static void main(String[] args) {
        LogTest logTest = new LogTest();
        logTest.logTest();
    }
}
