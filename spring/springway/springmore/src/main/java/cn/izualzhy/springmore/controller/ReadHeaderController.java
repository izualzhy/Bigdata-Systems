package cn.izualzhy.springmore.controller;

import cn.izualzhy.springmore.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ReadHeaderController {
    /**
     * curl -H "id: 123456" -H "Accept: version = 1.0" -H "Accept: tag = zyi" -X POST 'http://127.0.0.1:8001/header/user'
     * @param id id
     * @return {@link User}
     */
    @PostMapping("/header/user")
    @ResponseBody
    public User headerUser(@RequestHeader(value = "id") Long id) {
        System.out.println("id = " + id);
        return new User(id, "name_" + id, "note_" + id);
    }
}
