package cn.izualzhy.springmore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/interceptor")
public class InterceptorController {
    @GetMapping("/start")
    @ResponseBody
    public String start() {
        System.out.println("in interceptor controller");
        return "success";
    }
}
