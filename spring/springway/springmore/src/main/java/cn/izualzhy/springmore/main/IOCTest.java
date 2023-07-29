package cn.izualzhy.springmore.main;

import cn.izualzhy.springmore.config.UserAppConfig;
import cn.izualzhy.springmore.pojo.ScopeBean;
import cn.izualzhy.springmore.pojo.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IOCTest {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(UserAppConfig.class);
        User user;
//        user = applicationContext.getBean(User.class);
//        System.out.println(user.getId());

        user = (User) applicationContext.getBean("user");
        System.out.println(user.getId());

//        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ScopeBean scopeBean1 = applicationContext.getBean(ScopeBean.class);
        ScopeBean scopeBean2 = applicationContext.getBean(ScopeBean.class);
        System.out.println(scopeBean1);
        System.out.println(scopeBean2);
        System.out.println(scopeBean1 == scopeBean2);

        user = (User) applicationContext.getBean("JeffDean");
        System.out.println(user.getId());
    }
}
