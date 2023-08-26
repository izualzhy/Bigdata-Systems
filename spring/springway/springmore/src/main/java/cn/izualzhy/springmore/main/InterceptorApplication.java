package cn.izualzhy.springmore.main;


import cn.izualzhy.springmore.intercept.Interceptor1;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@SpringBootApplication
public class InterceptorApplication implements WebMvcConfigurer {
    public static void main(String[] args) {
        SpringApplication.run(InterceptorApplication.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(new Interceptor1());
        interceptorRegistration.addPathPatterns("/interceptor/*", "/user/*");
    }
}
