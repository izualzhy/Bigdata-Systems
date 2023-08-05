package cn.izualzhy.springmvc.controller;

import cn.izualzhy.springmvc.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserMvcController {
    @Autowired
    ViewResolver viewResolver;

    @RequestMapping("/details")
    public ModelAndView details(Long id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/details");
        modelAndView.addObject("user", "test_user");

        System.out.println("hasView : " + modelAndView.hasView() +
                " getViewName : " + modelAndView.getViewName() +
                " getView : " + modelAndView.getView() +
                " status : " + modelAndView.getStatus());

        System.out.println("viewResolver : " + viewResolver);

        ContentNegotiatingViewResolver viewer = (ContentNegotiatingViewResolver) viewResolver;
        for (View view : viewer.getDefaultViews()) {
            System.out.println("view : " + view);
        }

        return modelAndView;
    }

    @RequestMapping("/detailsForJson")
    public ModelAndView detailsForJson(Long id) {
        // 模型和视图
        ModelAndView mv = new ModelAndView();
        // 生成JSON视图
        MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
        mv.setView(jsonView);
        // 加入模型
        mv.addObject("user", "test_json_user");
        return mv;
    }

    @RequestMapping("/hello")
    public ModelAndView hello() {
        ModelAndView modelAndView = new ModelAndView("hello");
        modelAndView.addObject("message", "Hello, Spring MVC!");
        return modelAndView;
    }

    @RequestMapping("/table")
    public ModelAndView table() {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            userList.add(new User(i, "name_" + i, "note_" + i));
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/table");
        modelAndView.addObject("userList", userList);

        return modelAndView;
    }
}
