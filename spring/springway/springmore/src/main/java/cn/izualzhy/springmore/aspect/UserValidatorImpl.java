package cn.izualzhy.springmore.aspect;

import cn.izualzhy.springmore.pojo.User;

public class UserValidatorImpl implements UserValidator {
    @Override
    public boolean validate(User user) {
        System.out.println("引入 validate 接口：" + UserValidator.class.getSimpleName());
        return user != null;
    }
}
