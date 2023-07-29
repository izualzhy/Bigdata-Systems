package cn.izualzhy.springmore.db;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.xml.crypto.Data;

@Component
public class DataSourceShow implements ApplicationContextAware {
    ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        DataSource dataSource = applicationContext.getBean(DataSource.class);

        System.out.println("-----------------------------");
        System.out.println(dataSource.getClass().getName());
        System.out.println("-----------------------------");
    }
}
