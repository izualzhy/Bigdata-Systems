package cn.izualzhy.springmore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/my")
public class MyController {
    @GetMapping("/no/annotation")
    @ResponseBody
    public Map<String, Object> noAnnotations(Integer intVal, Long longVal, String str) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("intVal", intVal);
        paramsMap.put("longVal", longVal);
        paramsMap.put("str", str);

        return paramsMap;
    }

    @GetMapping("/annotation")
    @ResponseBody
    public Map<String, Object> requestParams(
            @RequestParam("int_val") Integer intVal,
            @RequestParam(value = "long_val", required = false) Long longVal,
            @RequestParam("str") String str) {
        Map<String, Object> paramsMap = new HashMap<>();

        paramsMap.put("intVal", intVal);
        paramsMap.put("longVal", longVal);
        paramsMap.put("str", str);

        return paramsMap;
    }
}
