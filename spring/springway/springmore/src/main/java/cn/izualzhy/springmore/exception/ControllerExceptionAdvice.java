package cn.izualzhy.springmore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionAdvice {
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> exception(HttpServletRequest request, NotFoundException ex) {
        Map<String, Object> exceptionMap = new HashMap<>();

        exceptionMap.put("code", ex.getCode());
        exceptionMap.put("message", ex.getCustomMessage());
        exceptionMap.put("advice by", this.getClass());

        return exceptionMap;
    }
}
