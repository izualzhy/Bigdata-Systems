package cn.izualzhy;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Student {
    public Student(String name, double score) {
        this.name = name;
        this.score = score;
    }

    static String getCollegeName() {
        return "zhangsan";
    }

    String getName() {
        return name;
    }
    void setName(String name) {
        this.name = name;
    }
    double getScore() {
        return score;
    }
    void setScore(double score) {
        this.score = score;
    }

    String name;
    double score;

    boolean foo(String s, double d) {
        System.out.println("foo s:" + s + " d:" + d + "\toriginal name:" + this.name + " score:" + this.score);
        this.name = s;
        this.score = d;

        return true;
    }

    @FunctionalInterface
    interface FooFunction {
        boolean func(String s, double d);
    }

    interface StudentFooFunction {
        boolean func(Student student, String s, double d);
    }

    public static void main(String[] args) {
        Supplier<String> s = Student::getCollegeName;
        s = () -> Student.getCollegeName();

        Function<Student, String> f = Student::getName;
        f = (Student t) -> t.getName();

        BiConsumer<Student, String> bc = Student::setName;
        bc = (t, name) -> t.setName(name);

        Student student = new Student("Jeff Dean", 101.0);
        FooFunction fooFunction = student::foo;
        fooFunction.func("hello", 102.0);

        StudentFooFunction studentFooFunction = Student::foo;
        studentFooFunction.func(student, "world", 103.0);
    }
}
