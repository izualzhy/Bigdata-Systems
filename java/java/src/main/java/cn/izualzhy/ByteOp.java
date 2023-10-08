package cn.izualzhy;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ByteOp {
    public static void main(String[] args) {
        byte[] data = new byte[256];
        data[0] = (byte) 0xbabe;

        byte[] data1 =  "helloworld".getBytes();
        System.arraycopy(data, 16, data1, 0, data1.length);

        for (int i = 1; i < 26; i++) {
            data[i] = (byte) i;
        }

        for (int i = 0; i < 26; i++) {
            System.out.printf("%02X ", data[i]);
        }
        System.out.println(Arrays.toString(data));

        System.out.println("\n" + new String(data, StandardCharsets.UTF_8));
    }
}
