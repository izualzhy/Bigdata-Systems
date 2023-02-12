package cn.izualzhy.spring;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class ParamController {
    @GetMapping("/noannotation") // @GetMapping=@RequestMapping(method=RequestMethod.GET)
    public User noAnnotation(User user) {
        return user;
    }

    @GetMapping("/requestparam")
    public User requestParam(@RequestParam String name, @RequestParam(required = true) int age) {
        User user = new User();
        user.setName(name);
        user.setAge(age);

        return user;
    }

    @GetMapping("/pathvariable/{name}/{age}")
    public User pathVariable(@PathVariable String name, @PathVariable int age) {
        User user = new User();
        user.setName(name);
        user.setAge(age);

        return user;
    }

    @PostMapping("/requestbody")
    public User requestBody(@RequestBody @Valid User user) {
        return user;
    }
}
