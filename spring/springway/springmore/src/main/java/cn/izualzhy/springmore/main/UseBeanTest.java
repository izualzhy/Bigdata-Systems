package cn.izualzhy.springmore.main;

import cn.izualzhy.springmore.config.UserAppConfig;
import cn.izualzhy.springmore.pojo.AnotherELClass;
import cn.izualzhy.springmore.pojo.SampleELClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class UseBeanTest {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(UserAppConfig.class);

        SampleELClass elClass = (SampleELClass) applicationContext.getBean(SampleELClass.class);
        System.out.println(elClass);

        AnotherELClass anotherELClass = (AnotherELClass) applicationContext.getBean("anotherELClass");
        System.out.println(anotherELClass);

        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            System.out.println("------");
            System.out.println(beanName + " : " + bean.getClass().toString() + " object:\n" + bean);
        }
    }
}
