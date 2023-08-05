package cn.izualzhy.springmore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController extends BaseController {

    @PostMapping("/hello")
    public String helloSample(@RequestParam("world") String world) {
        return "hello " + world + "\n Welcome to new world.";
    }
    @GetMapping(path = "helloWorld")
    public String helloWorld() {
        return "hello  world.";
    }
}
