package cn.izualzhy;

public class AutoCloseableTest {
    public static class Resource implements AutoCloseable {
        public void read() {
            System.out.println(this + " read.");
        }

        @Override
        public void close() throws Exception {
            System.out.println(this + " close.");
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("enter");
        try (Resource resource = new Resource()){
//            resource.read();
            int a = 1/0;
//            return;
        }

//        System.out.println("wait");
//        Thread.sleep(2000L);
//        Resource resource = new Resource();
//        Thread.sleep(2000L);
//        System.out.println("leave");
    }
}
