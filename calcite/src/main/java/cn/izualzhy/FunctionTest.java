package cn.izualzhy;

import java.util.function.Function;

public class FunctionTest {
    class Foo {
        public int bar(String who) {
            String s = who + ": Hello World";
            System.out.println(s);
            return s.length();
        }
    }

    public static void main(String[] args) {
//        Function<String, int> f = "I" -> 1;
    }

}
