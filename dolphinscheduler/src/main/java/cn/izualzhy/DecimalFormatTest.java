package cn.izualzhy;

import java.text.DecimalFormat;

public class DecimalFormatTest {
    public static void main(String[] args) {
        DecimalFormat decimalFormat = new DecimalFormat("00000.00");
        System.out.println(decimalFormat.format(6789.8765));

        decimalFormat = new DecimalFormat("#,##,###, ##00.00");
        System.out.println(decimalFormat.format(123456789.9876543));
        System.out.println(decimalFormat.format(1.98765432));

        decimalFormat = new DecimalFormat("0.#");
        System.out.println(decimalFormat.format(123456789.9876543));
        System.out.println(decimalFormat.format(1.98765432));
        System.out.println(decimalFormat.format(1.100));
        System.out.println(decimalFormat.format(1.00));
        System.out.println(decimalFormat.format(1.003333333333333));
        System.out.println(decimalFormat.format(5.000001));
    }
}
