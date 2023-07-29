package cn.izualzhy.springmore.aspect;

import cn.izualzhy.springmore.pojo.User;

public interface UserValidator {
    public boolean validate(User user);
}
