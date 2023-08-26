package cn.izualzhy.springmore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class RedirectController {
    private String redirectUrl = "/v1/base/helloWorld";
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

    @GetMapping("/redirectA")
    public String redirectA() {
        return "redirect:/new-url";
    }

    @GetMapping("/new-url")
    public void newUrl(HttpServletResponse response) throws IOException {
        // 处理新的URL逻辑，不返回视图
        response.sendRedirect(redirectUrl);
//        response.sendRedirect("https://izualzhy.cn");
    }
}
