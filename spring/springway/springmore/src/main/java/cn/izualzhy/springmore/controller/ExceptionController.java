package cn.izualzhy.springmore.controller;


import cn.izualzhy.springmore.exception.NotFoundException;
import cn.izualzhy.springmore.pojo.User;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
//@EnableWebSecurity
public class ExceptionController {
    @GetMapping("/test/user")
    @ResponseBody
    User userById(Long id) {
        if (id == null || id <= 0) {
            throw new NotFoundException(1L, "can not find user:" + id);
        }
        return new User(id, "name_" + id, "note_" + id);
    }
}
