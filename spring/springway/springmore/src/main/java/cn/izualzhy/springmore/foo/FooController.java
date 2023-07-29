package cn.izualzhy.springmore.foo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FooController {
    @GetMapping(path = "/foo")
    public String helloWorld() {
        return "foo.";
    }
}
