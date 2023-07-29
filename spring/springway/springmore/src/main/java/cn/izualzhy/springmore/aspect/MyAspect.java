package cn.izualzhy.springmore.aspect;

import cn.izualzhy.springmore.pojo.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MyAspect {
    @DeclareParents(value = "cn.izualzhy.springmore.service.impl.UserServiceImpl", defaultImpl = UserValidatorImpl.class)
    public UserValidator userValidator;

//    @Pointcut("execution(* cn.izualzhy.springmore.controller.UserController.printUser(..))")
//    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    @Pointcut("execution(* cn.izualzhy.springmore.service.impl.UserServiceImpl.printUser(..))")
    public void pointCut() {
    }

    @Before("pointCut() && args(user)")
    public void beforeParam(JoinPoint point, User user) {
        Object[] args = point.getArgs();
        System.out.println("before ......");
    }

//    @Before("execution(* cn.izualzhy.springmore.service.impl.UserServiceImpl.printUser(..))")
    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {
        System.out.println("before ......" + joinPoint.toString());
    }

    //    @After("execution(* cn.izualzhy.springmore.service.impl.UserServiceImpl.printUser(..))")
    @After("pointCut()")
    public void after() {
        System.out.println("after ......");
    }

    //    @AfterReturning("execution(* cn.izualzhy.springmore.service.impl.UserServiceImpl.printUser(..))")
    @AfterReturning("pointCut()")
    public void afterReturning() {
        System.out.println("afterReturning ......");
    }

    //    @AfterThrowing("execution(* cn.izualzhy.springmore.service.impl.UserServiceImpl.printUser(..))")
    @AfterThrowing("pointCut()")
    public void afterThrowing() {
        System.out.println("afterThrowing ......");
    }

    @Around("pointCut()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before ......");
        joinPoint.proceed();
        System.out.println("around after  ......");
    }
}
