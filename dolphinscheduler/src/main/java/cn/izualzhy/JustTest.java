package cn.izualzhy;

import org.apache.commons.io.FileUtils;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.SECONDS;


public class JustTest {
    static class Foo implements Runnable {
        @Override
        public void run() {
            String hiveCliServiceProtocolVersion = System.getenv("HIVE_ClI_SERVICE_PROTOCOL_VERSION");
            if (hiveCliServiceProtocolVersion == null) {
                hiveCliServiceProtocolVersion = "default";
            }
            System.out.println(
                    "thread : " + Thread.currentThread() +
                            "env : " + hiveCliServiceProtocolVersion);
        }
    }
    public static void main(String[] args) {
        String[]  forbiddenSqlRegex = {
                // 设置队列名
                "\\s*set\\s+mapred(uce)?.job.queuename\\s*=\\s*\\S+\\s*",
                "\\s*set\\s+tez.queue.name\\s*=\\s*\\S+\\s*"
        };
        String sql = "set mapred.job.queuename=ai-core";
        for (String regex : forbiddenSqlRegex) {
            if (Pattern.matches(regex, sql.toLowerCase())) {
                System.out.println("matched.");
            }
        }

        Map<String, Integer> m1 = new HashMap<>();
        m1.put("a", 1);
        m1.put("b", 1);
        m1.put("c", 1);
        Map<String, Integer> m2 = new HashMap<>();
        m2.put("b", 2);
        m2.put("c", 3);
        m2.put("d", 4);

        m1.putAll(m2);

        for (Map.Entry<String, Integer> entry : m1.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }


        String appName = "kyuubi://dummyhost:00000/default?spark.yarn.queue=bigdata-default;kyuubi.operation.progress.enabled=true;spark.app.name=134442#458217#test_kyuubi#202305251125.1684985114039";
        System.out.println(appName.replaceAll("#|@|\\.", "_"));
    }
}
