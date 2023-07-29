package cn.izualzhy.springmore.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(scanBasePackages = {"cn.izualzhy.springmore"})
@SpringBootApplication(scanBasePackages = {"cn.izualzhy.springmore.controller", "cn.izualzhy.springmore.service", "cn.izualzhy.springmore.aspect"})
public class MyAspectApplication {
//    @Bean(name = "myAspect")
//    public MyAspect initMyAspect() {
//        return new MyAspect();
//    }

    public static void main(String[] args) {
        SpringApplication.run(MyAspectApplication.class, args);
    }
}