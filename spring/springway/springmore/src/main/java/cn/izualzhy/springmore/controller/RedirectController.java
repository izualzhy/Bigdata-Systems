package cn.izualzhy.springmore.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class RedirectController {
    private String redirectUrl = "/dolphinscheduler/v1/base/helloWorld";
    @GetMapping("/redirect")
//    public void method(HttpServletResponse httpServletResponse) {
//        System.out.println("redirect");
//
//        httpServletResponse.setHeader("Location", redirectUrl);
//        httpServletResponse.setStatus(302);
//    }

    public ModelAndView method() {
        return new ModelAndView("redirect:" + redirectUrl);
    }
}
