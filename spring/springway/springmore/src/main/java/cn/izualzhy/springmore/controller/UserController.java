package cn.izualzhy.springmore.controller;

import cn.izualzhy.springmore.aspect.UserValidator;
import cn.izualzhy.springmore.pojo.User;
import cn.izualzhy.springmore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService = null;

    @RequestMapping("/print")
    @ResponseBody
    public User printUser(Long id, String userName, String note) {
        User user = new User();
        user.setId(id);
        user.setUserName(userName);
        user.setNote(note);

        userService.printUser(user);
        return user;
    }

    @GetMapping("/test")
    public void test() {
        System.out.println("test");
    }

    @RequestMapping("/validate")
    @ResponseBody
    public User validateUser(Long id, String userName, String note) {
        User user = new User();
        user.setId(id);
        user.setUserName(userName);
        user.setNote(note);

        UserValidator userValidator = (UserValidator) userService;
        if (userValidator.validate(user)) {
            userService.printUser(user);
        }

        return user;
    }
}
