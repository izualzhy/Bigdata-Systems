package cn.izualzhy.springmore.controller;

import cn.izualzhy.springmore.pojo.User;
import cn.izualzhy.springmore.pojo.ValidatorPojo;
import cn.izualzhy.springmore.validator.UserValidator;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ValidatorController {
    @PostMapping(value = "/valid/validate")
    @ResponseBody
    public Map<String, Object> validate(@Valid @RequestBody ValidatorPojo validatorPojo, Errors errors) {
        Map<String, Object> errMap = new HashMap<>();
        List<ObjectError> oes = errors.getAllErrors();

        for (ObjectError ores : oes) {
            String key = null;
            String msg = null;

            if (ores instanceof FieldError) {
                FieldError fe = (FieldError) ores;
                key = fe.getField();
            } else {
                key = ores.getObjectName();
            }

            msg = ores.getDefaultMessage();
            errMap.put(key, msg);
        }

        System.out.println(validatorPojo);

        return errMap;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(new UserValidator());

        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), false));
    }

    @GetMapping("/validator")
    @ResponseBody
    public Map<String, Object> validator(@Valid User user, Errors errors, Date date) {
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("date", date);

        if (errors.hasErrors()) {
            List<ObjectError> oes = errors.getAllErrors();
            for (ObjectError oe : oes) {
                if (oe instanceof FieldError) {
                    FieldError fe = (FieldError) oe;
                    map.put(fe.getField(), fe.getDefaultMessage());
                } else {
                    map.put(oe.getObjectName(), oe.getDefaultMessage());
                }
            }
        }

        return map;
    }
}
