package cn.izualzhy.springmore.pojo;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ScopeBean {
}
