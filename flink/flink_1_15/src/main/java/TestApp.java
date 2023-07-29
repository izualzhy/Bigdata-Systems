public class TestApp {
    public static void main(String[] args) throws Exception {
        if (args == null || args.length < 1) {
            throw new Exception("请指定metric exporter端口");
        }

        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }
    }
}
