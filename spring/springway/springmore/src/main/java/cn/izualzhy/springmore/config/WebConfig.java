package cn.izualzhy.springmore.config;

import cn.izualzhy.springmore.converter.StringToUserConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/v1",
                HandlerTypePredicate.forAnnotation(RestController.class));
    }
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToUserConverter());
    }
}
