package cn.izualzhy.springmore.converter;

import cn.izualzhy.springmore.pojo.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToUserConverter implements Converter<String, User> {
    @Override
    public User convert(String userStr) {
        User user = new User();

        String[] strArr = userStr.split("-");
        Long id = Long.parseLong(strArr[0]);
        String userName = strArr[1];
        String note = strArr[2];

        user.setId(id);
        user.setUserName(userName);
        user.setNote(note);

        System.out.println("user:" + user);
        return user;
    }
}
