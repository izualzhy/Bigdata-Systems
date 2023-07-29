package cn.izualzhy.springmore.service.impl;

import cn.izualzhy.springmore.pojo.User;
import cn.izualzhy.springmore.service.UserService;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Pointcut("execution(* cn.izualzhy.springmore.service.UserService.printUser(..))")
    public void printUserPointcut() {}

    @Before("printUserPointcut()")
    public void beforePrintUser() {
        System.out.println("在printUser方法执行之前执行...");
    }

    private User user = null;

    public void printUser(User user) {
        if (user == null) {
            throw new RuntimeException("检查用户参数是否为空......");
        }

        System.out.println("id = " + user.getId());
        System.out.println("\tusername = " + user.getUserName());
        System.out.println("\tnote = " + user.getNote());
    }
    @Override
    public void printUser() {
        if (user == null) {
            throw new RuntimeException("检查用户参数是否为空......");
        }
        System.out.print("id =" + user.getId());
        System.out.print("\tusername =" + user.getUserName());
        System.out.println("\tnote =" + user.getNote());

    }
}
