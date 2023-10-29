package cn.izualzhy.springmore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;

@Component

@Validated
public class MasterConfig implements Validator {
    MasterConfig() {
        System.out.println("MasterConfig Constructor.");
    }
    @Override
    public boolean supports(Class<?> clazz) {
        return MasterConfig.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MasterConfig masterConfig = (MasterConfig) target;
        System.out.printf("MasterConfig(%s).validate \n", masterConfig);
    }
}
