package cn.izualzhy.springmore.main;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;

@MapperScan(
        basePackages = "cn.izualzhy.springmore",
        annotationClass = Repository.class
)
@SpringBootApplication(scanBasePackages = {"cn.izualzhy.springmore"})
public class TransactionTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransactionTestApplication.class, args);
    }

    @Autowired
    PlatformTransactionManager transactionManager = null;

    @PostConstruct
    public void viewTransactionManager() {
        System.out.println("transactionManager : " + transactionManager.getClass().getName());

        if (transactionManager instanceof JdbcTransactionManager) {
            JdbcTransactionManager jdbcTransactionManager = (JdbcTransactionManager) transactionManager;
            System.out.println("dataSource : " + jdbcTransactionManager.getDataSource());
        }
    }
}
