package cn.izualzhy.controll;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private final InetUtils inetUtils;

    @Autowired
    HelloController(InetUtils inetUtils) {
        this.inetUtils = inetUtils;
    }

    @GetMapping("/hello")
    String hello() {
        String localHost = inetUtils.findFirstNonLoopbackAddress().getHostAddress();
        return String.format("@%s > hello.", localHost);
    }
}
