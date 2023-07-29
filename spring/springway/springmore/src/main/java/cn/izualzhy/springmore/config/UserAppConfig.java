package cn.izualzhy.springmore.config;

import cn.izualzhy.springmore.pojo.User;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan("cn.izualzhy.springmore.pojo")
public class UserAppConfig {
    @Bean(name = "user")
    @Primary
    public User initUser() {
        User user = new User();
        user.setId(123456L);
        user.setUserName("user_name_123456");
        user.setNote("note_123456");

        return user;
    }

    @Bean(name = "JeffDean")
    @Profile("test")
    public User initTestUser() {
        User user = new User();
        user.setId(654321L);
        user.setUserName("test_user_name_123456");
        user.setNote("test_note_123456");

        return user;
    }

    @Bean(name = "JeffDean")
    @Profile("online")
    public User initOnlineUser() {
        User user = new User();
        user.setId(96731543750L);
        user.setUserName("JeffDean");
        user.setNote("google");

        return user;
    }
}
