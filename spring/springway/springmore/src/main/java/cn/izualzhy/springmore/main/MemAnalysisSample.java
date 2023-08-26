package cn.izualzhy.springmore.main;

import java.util.HashMap;

public class MemAnalysisSample {

    static class MemEater {
        private String mem = null;
        MemEater(String mem) {
            this.mem = mem;
        }
    }

    public static void main(String[] args) {
        System.out.println("enter main");
        HashMap<String, MemEater> map = new HashMap<>();

        try {
            for (int i = 0; i < 100; ++i) {
                System.out.println("i:" + i);
                String key = generateRandomString();
                String value = generateRandomString();
                map.put(key, new MemEater(value));
                Thread.sleep(30000);
            }
        } catch (OutOfMemoryError e) {
            System.out.println("Out of memory error occurred.");
        } catch (InterruptedException e) {
            System.out.println("InterruptedException occurred.");
        }

        System.out.println("leave main");
    }

    private static String generateRandomString() {
        StringBuilder sb = new StringBuilder();
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        int length = 50000; // 控制字符串长度，增加内存占用

        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(randomIndex));
        }

        return sb.toString();
    }
}
