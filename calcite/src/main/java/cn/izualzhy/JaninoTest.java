package cn.izualzhy;

import org.codehaus.commons.compiler.*;

import java.lang.reflect.Method;

public class JaninoTest {
    public static void ExpressionEvaluator() {
        try {
            IExpressionEvaluator ee = CompilerFactoryFactory.getDefaultCompilerFactory().newExpressionEvaluator();
            ee.setExpressionType(int.class);
            ee.setParameters(
                    new String[] {"a", "b"},
                    new Class[] {int.class, int.class}
            );

            ee.cook("a + b");
            Integer res = (Integer) ee.evaluate(
                    new Object[] {10, 11}
            );
            System.out.println("res = " + res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ScriptEvaluator() {
        try {
            IScriptEvaluator se = CompilerFactoryFactory.getDefaultCompilerFactory().newScriptEvaluator();
            se.setReturnType(boolean.class);
            se.cook("static void method1() {\n"
                    + "    System.out.println(1);\n"
                    + "}\n"
                    + "\n"
                    + "method1();\n"
                    + "method2();\n"
                    + "\n"
                    + "static void method2() {\n"
                    + "    System.out.println(2);\n"
                    + "}\n"
                    + "return true;\n"
            );
            Object res = se.evaluate(new Object[0]);
            System.out.println("res = " + res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ClassBodyEvaluator() {
        try {
            IClassBodyEvaluator cbe = CompilerFactoryFactory.getDefaultCompilerFactory().newClassBodyEvaluator();
            String code = ""
                    + "public static void main(String[] args) {\n"
                    + "    System.out.println(java.util.Arrays.asList(args));\n"
                    + "}";
            cbe.cook(code);
            Class<?> c = cbe.getClazz();
            Method mainMethod = c.getMethod("main", String[].class);
            String[] args = {"Hello", "World", "Hello", "Janino"};
            mainMethod.invoke(null, (Object)args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void SimpleCompilerEvaluator() {
        try {
            ISimpleCompiler simpleCompiler = CompilerFactoryFactory.getDefaultCompilerFactory().newSimpleCompiler();
            String code = "" +
                    "public class Foo {\n" +
                    "    public static void main(String[] args) {\n" +
                    "        new Bar().meth(\"Foo\");\n" +
                    "    }\n" +
                    "}\n" +
                    "\n" +
                    "public class Bar {\n" +
                    "    public void meth(String who) {\n" +
                    "        System.out.println(\"Hello! \" + who);\n" +
                    "    }\n" +
                    "}";
            simpleCompiler.cook(code);
            Class<?> fooClass = simpleCompiler.getClassLoader().loadClass("Foo");
            fooClass.getDeclaredMethod("main", String[].class).invoke(null, (Object)new String[]{"hello", "world"});

            Class<?> barClass = simpleCompiler.getClassLoader().loadClass("Bar");
            barClass.getDeclaredMethod("meth", String.class).invoke(barClass.newInstance(), "Janino");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ExpressionEvaluator();
        ScriptEvaluator();
        ClassBodyEvaluator();
        SimpleCompilerEvaluator();
    }
}

