package cn.izualzhy;

public class MinorGC {
    public static void main(String[] args) {
        testAllocation();
    }

    private static final int _1MB = 1024 * 1024;
    /**
     * VM 参 数：-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
     */
    static void testAllocation() {
        byte[] allocation1, allocation2, allocation3, allocation4;

        System.out.println("allocation1");
        allocation1 = new byte[2 * _1MB];
        System.out.println("allocation2");
        allocation2 = new byte[2 * _1MB];
        System.out.println("allocation3");
        allocation3 = new byte[4 * _1MB];
        System.out.println("allocation4");
        allocation4 = new byte[4 * _1MB];
    }
}
