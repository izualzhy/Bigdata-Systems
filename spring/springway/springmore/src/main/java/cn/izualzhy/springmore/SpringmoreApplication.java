package cn.izualzhy.springmore;

import cn.izualzhy.springmore.scanner.RestApiScanner;
import cn.izualzhy.springmore.service.impl.ScheduleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
//@Import({HealthCheckWebConfig.class})
//@ServletComponentScan(basePackages = "cn.izualzhy.springmore.servlet")
public class SpringmoreApplication {
    @Autowired
    public RestApiScanner restApiScanner;
    @Autowired
    public ScheduleServiceImpl scheduleService;

    public static void main(String[] args) {
        SpringApplication.run(SpringmoreApplication.class, args);
    }

//    @Bean
//    public ServletRegistrationBean v1ServletRegistration() {
//        ServletRegistrationBean registrationBean = new ServletRegistrationBean(
//                new DispatcherServlet(),
//                "/v1/*");
//        registrationBean.setName(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME);
//        registrationBean.setLoadOnStartup(1);
//
//        return registrationBean;
//    }

    /**
     @Bean
     public ServletRegistrationBean foo() {
     DispatcherServlet dispatcherServlet = new DispatcherServlet();
     AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
     applicationContext.register(FooConfig.class);
     dispatcherServlet.setApplicationContext(applicationContext);
     ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet, "/foo/*");
     servletRegistrationBean.setLoadOnStartup(1);
     servletRegistrationBean.setName("health_controller");
     return servletRegistrationBean;
     }
     */
}
