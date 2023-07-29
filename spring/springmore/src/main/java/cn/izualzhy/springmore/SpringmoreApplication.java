package cn.izualzhy.springmore;

import cn.izualzhy.springmore.servlet.HealthCheckWebConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({HealthCheckWebConfig.class})
//@ServletComponentScan(basePackages = "cn.izualzhy.springmore.servlet")
public class SpringmoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringmoreApplication.class, args);
    }

}
