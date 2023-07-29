package cn.izualzhy;

public class AutoCloseableTest {
    public static class Resource implements AutoCloseable {
        String name;

        Resource(String name) {
            this.name = name;
            System.out.println("Resource constructor " + this);
        }

        void read() {
            if (this.name.contains("EXCEPTION_MARK")) {
                throw new RuntimeException("contains EXCEPTION_MARK");
            }
            System.out.println(this + " read.");
        }

        @Override
        public void close() {
            System.out.println(this + " close.");
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static void main(String[] args) throws Exception {
        // 先创建、后销毁
        try (Resource resource1 = new Resource("a");
             Resource resource2 = new Resource("EXCEPTION_MARK")) {
            resource1.read();
            resource2.read();
        }
    }
}
