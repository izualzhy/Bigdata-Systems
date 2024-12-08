package cn.izualzhy.utils;

import cn.izualzhy.client.UserFacade;
import cn.izualzhy.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class FeignSample {
    @Autowired
    private UserFacade userFacade;

    @PostConstruct
    void sample() {
        User user = userFacade.getUser(1L);
        System.out.println(user);

        user = userFacade.getUserInfoResponse(123L);
        System.out.println(user);
    }

}
