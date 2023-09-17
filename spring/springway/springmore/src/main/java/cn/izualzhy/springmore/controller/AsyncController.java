package cn.izualzhy.springmore.controller;

import cn.izualzhy.springmore.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AsyncController {
    @Autowired
    private AsyncService asyncService;

    @GetMapping(value = "/async/report")
    @ResponseBody
    String asyncReport() throws InterruptedException {
        System.out.println("controller in thread:" + Thread.currentThread().getName());
        asyncService.generateReport();

        return "success";
    }
}
