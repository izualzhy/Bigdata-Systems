package cn.izualzhy;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class JVMMemTest {
    static class OOMObject {
    }

    static void heapOOM() {
        /*
        VM Args: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
         */
        List<OOMObject> list = new ArrayList<OOMObject>();
        while (true) {
            list.add(new OOMObject());
        }
    }

    static void stackLeak() {
        /*
        VM Args: -Xss256k
         */
        stackLeak();
    }

    static void runForever() {
        boolean first = true;
        while (true) {
            if (first) {
                System.out.println(Thread.currentThread());
                first = false;
            }
        }
    }

    static void stackLeakByThread() {
        /*
        VM Args: -Xss2M
        未复现
         */
        while (true) {
            System.out.println("create new thread.");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    runForever();
                }
            });
            thread.start();
        }
    }

    static void stackSOF() {
        stackLeak();
//        stackLeakByThread();
    }

    static void directMemoryOOM() {
        try {
            Field unsafeField = Unsafe.class.getDeclaredFields()[0];
            unsafeField.setAccessible(true);

            Unsafe unsafe = (Unsafe) unsafeField.get(null);
            int i = 0;
            while (true) {
                i += 1;
                long address = unsafe.allocateMemory(10 * 1024 * 1024);
                long maxMemory = Runtime.getRuntime().maxMemory();
                long totalMemory = Runtime.getRuntime().totalMemory();
                long freeMemory = Runtime.getRuntime().freeMemory();

                System.out.println(String.format("allocate %d * 10M, address:%d, memory:%d %d %d", i, address, totalMemory, maxMemory, freeMemory));
                unsafe.setMemory(address, 10 * 1024 * 1024, (byte) 0);

                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    static void directMemoryOOM2() {
        try {
            ArrayList al = new ArrayList();
            int i = 0;
            while (true) {
                i += 1;
                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(10 * 1024 * 1024);
                al.add(byteBuffer);
                System.out.println("Buffer loop " + i);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
//        heapOOM();
//        stackSOF();
//        directMemoryOOM();
        directMemoryOOM2();
    }
}
