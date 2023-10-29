package cn.izualzhy.springmore.main;

import cn.izualzhy.springmore.config.MasterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.validation.Valid;

@SpringBootApplication(scanBasePackages = "cn.izualzhy.springmore.config")
public class JustTest {
    @Autowired
    @Valid
    MasterConfig masterConfig;
    public static void main(String[] args) {
        SpringApplication.run(JustTest.class, args);
    }
}
