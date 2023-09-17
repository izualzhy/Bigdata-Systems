package cn.izualzhy.springmore.autowired;

import cn.izualzhy.springmore.SpringmoreApplication;
import cn.izualzhy.springmore.context.SpringApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class AutoWiredTest {
    public class ClassA {}
    public class ClassB {}
    public class ClassC {}
    public interface TaskPriority<T> {
    }
    @Service
    public class TaskPriorityA implements TaskPriority<ClassA> {
        TaskPriorityA() {
            System.out.println(getClass().getName() + " " + this);
        }
    }
    @Service
    public class TaskPriorityB implements TaskPriority<ClassB> {
        TaskPriorityB() {
            System.out.println(getClass().getName() + " " + this);
        }
    }

    @Service
    public class TaskPriorityC implements TaskPriority<ClassC> {
        TaskPriorityC() {
            System.out.println(getClass().getName() + " " + this);
        }
    }

    @Component
    public class SampleClass {
        @Autowired
        public TaskPriority<ClassC> taskPriority;
        @Autowired
        public TaskPriority<ClassB> taskPriority2;

        SampleClass() {
        }

        @PostConstruct
        void init() {
            System.out.println("getBean:" + SpringApplicationContext.getBean(TaskPriorityC.class));
            System.out.println("taskPriority:" + taskPriority);

            System.out.println("getBean:" + SpringApplicationContext.getBean(TaskPriorityB.class));
            System.out.println("taskPriority:" + taskPriority2);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringmoreApplication.class, args);
    }
}
