package cn.izualzhy.springmore.main;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Repository;

@SpringBootApplication(scanBasePackages = "cn.izualzhy")
@MapperScan(basePackages="cn.izualzhy", annotationClass = Repository.class)
public class JspTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(JspTestApplication.class, args);
    }
}
