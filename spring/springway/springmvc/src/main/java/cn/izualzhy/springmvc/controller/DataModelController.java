package cn.izualzhy.springmvc.controller;


import cn.izualzhy.springmvc.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/data")
@Controller
public class DataModelController {
    @GetMapping("/model1")
    public String useModel(Model model) {
        User user = new User(1, "pingan", "xiangfu");
        model.addAttribute("user", user);

        return "data/user";
    }

    @GetMapping("/modelMap")
    public ModelAndView useModelMap(ModelMap modelMap) {
        User user = new User(2, "jiankang", "xiangfu");
        ModelAndView mav = new ModelAndView();
        mav.setViewName("data/user");
        modelMap.put("user", user);

        return mav;
    }

    @GetMapping("/mav")
    public ModelAndView useModelAndView(ModelAndView mav) {
        User user = new User(3, "changshou", "xiangfu");
        mav.addObject("user", user);
        mav.setViewName("data/user");

        return mav;
    }
}
