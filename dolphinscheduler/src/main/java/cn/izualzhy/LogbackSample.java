package cn.izualzhy;

import org.slf4j.*;

public class LogbackSample {
    static Logger logger = LoggerFactory.getLogger(LogbackSample.class);
//    static Marker FINALIZE_SESSION_MARKER = MarkerFactory.getMarker("FINALIZE_SESSION");
//    static Marker TEST_MARKER = MarkerFactory.getMarker("TEST_MARKER");

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(){
            public void run() {
                logger.info("this is a log before MDC.");
                MDC.put("taskId", "12315");
                logger.info("this is a log IN MDC.");
                MDC.remove("taskId");
                logger.info("this is a log after MDC.");
            }
        };
        thread.start();
        thread.join();

//        logger.info(FINALIZE_SESSION_MARKER, "info with FINALIZE_SESSION_MARKER");
//        logger.info(TEST_MARKER, "info with TEST_MARKER");
    }
}
