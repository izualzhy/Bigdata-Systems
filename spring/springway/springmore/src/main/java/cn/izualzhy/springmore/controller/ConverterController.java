package cn.izualzhy.springmore.controller;

import cn.izualzhy.springmore.pojo.User;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 转换器控制器
 *
 * @author zhangying
 * @date 2023/08/08
 */
@RestController
//@Controller
public class ConverterController {
//    @GetMapping(value = "/converter")
    @RequestMapping("/converter")
    @ResponseBody
    public User getUserByConverter(User user) {

        return user;
    }

    @GetMapping("/list")
    @ResponseBody
    List<User> list(List<User> userList) {
        return userList;
    }
}
