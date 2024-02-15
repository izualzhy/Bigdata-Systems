package cn.izualzhy;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class LambdaSample {
    @FunctionalInterface
    interface Filter {
        abstract boolean foo();
    }

    @FunctionalInterface
    interface Runner {
        abstract void run(String arg);
    }

    static void useAnonymousClass() {
        filterAsArgs(new Filter() {
            @Override
            public boolean foo() {
                System.out.println("implement AnonymousClass foo");

                return true;
            }
        });

        runnerAsArgs(new Runner() {
            @Override
            public void run(String arg) {
                System.out.println("implement AnonymousClass run arg:" + arg);
            }
        });
    }

    static void useLambda() {
        filterAsArgs(() -> {
            System.out.println("implement Lambda foo");
            return true;
        });

        runnerAsArgs(arg -> System.out.println("implement Lambda run arg:" + arg));
        consumerAsArgs(arg -> System.out.println("implement Lambda consume arg:" + arg));
    }

    void useFunction() {
        runnerAsArgs(LambdaSample::staticRunnerFunction);
        runnerAsArgs(this::runnerFunction);
    }

    void studentClassSample() {
        List<Student> students = Arrays.asList(new Student[] {
                new Student("zhangsan", 89d), new Student("lisi", 89d),
                new Student("wangwu", 98d) });
    }


    public static void main(String[] args) {
        useAnonymousClass();
        useLambda();
        LambdaSample lambdaSample = new LambdaSample();
        lambdaSample.useFunction();
    }

    static void filterAsArgs(Filter filter) {
        System.out.println("------ enter filterAsArgs ------");
        filter.foo();
        System.out.println("------ leave filterAsArgs ------");
    }
    static void runnerAsArgs(Runner runner) {
        System.out.println("------ enter runnerAsArgs ------");
        runner.run("hello world");
        System.out.println("------ leave runnerAsArgs ------");
    }

    static void consumerAsArgs(Consumer<String> consumer) {
        System.out.println("------ enter consumerAsArgs ------");
        consumer.accept("hello world");
        System.out.println("------ leave consumerAsArgs ------");

    }

    void runnerFunction(String arg) {
        System.out.println("implement function run arg:" + arg);
    }
    static void staticRunnerFunction(String arg) {
        System.out.println("implement static function run arg:" + arg);
    }
}
