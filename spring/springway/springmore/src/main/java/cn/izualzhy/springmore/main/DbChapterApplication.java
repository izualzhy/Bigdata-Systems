package cn.izualzhy.springmore.main;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Repository;

@SpringBootApplication(scanBasePackages = {"cn.izualzhy.springmore"})
@MapperScan(
        basePackages = "cn.izualzhy.springmore",
        sqlSessionFactoryRef = "sqlSessionFactory",
        sqlSessionTemplateRef = "sqlSessionTemplate",
        annotationClass = Repository.class
)
//@MapperScan("cn.izualzhy.springmore.mapper")
public class DbChapterApplication {
    public static void main(String[] args) {
        SpringApplication.run(DbChapterApplication.class, args);
    }
}
