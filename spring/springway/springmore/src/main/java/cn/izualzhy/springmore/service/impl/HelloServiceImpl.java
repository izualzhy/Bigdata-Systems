package cn.izualzhy.springmore.service.impl;

import cn.izualzhy.springmore.intercept.MyInterceptor;
import cn.izualzhy.springmore.proxy.ProxyBean;
import cn.izualzhy.springmore.service.HelloService;

public class HelloServiceImpl implements HelloService {
    @Override
    public void sayHello(String name) {
        if (name == null || name.trim() == "") {
            throw new RuntimeException("parameter is null!!!");
        }
        System.out.println("hello " + name);
    }

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        System.out.println("------ service say ------");
        helloService.sayHello("world");

        HelloService proxy = (HelloService) ProxyBean.getProxyBean(helloService, new MyInterceptor());
        System.out.println("------ proxy say ------");
        proxy.sayHello("world");

        System.out.println("------ proxy say ------");
        proxy.sayHello(null);
    }
}
