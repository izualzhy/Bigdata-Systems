package cn.izualzhy;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class SystemOverLoadTest {
    public static void main(String[] args) {
        System.out.println("availableProcessors = " + String.valueOf(Runtime.getRuntime().availableProcessors()));
        for (int i = 0; i < 10; i++) {
            double loadAverage = -1;
            try {
                OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
                loadAverage = osBean.getSystemLoadAverage();
                DecimalFormat df = new DecimalFormat("0.00");
                df.setRoundingMode(RoundingMode.HALF_UP);
                System.out.println(Double.parseDouble(df.format(loadAverage)));

                Thread.sleep(5000);
            } catch (Exception e) {

            }
        }
    }
}
