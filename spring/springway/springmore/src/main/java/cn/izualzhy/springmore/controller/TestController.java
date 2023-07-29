package cn.izualzhy.springmore.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("ufo")
public class TestController extends BaseController {
    @GetMapping(value = "/test")
    public String login(@RequestParam(value = "userName") String userName) {
        return userName + " test.";
    }
//    @PostMapping(value = "/test")
}
