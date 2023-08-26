package cn.izualzhy.springmore.scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class RestApiScanner {

    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    public RestApiScanner(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    public List<String> getAllRestApiPaths() {
        List<String> restApiPaths = new ArrayList<>();

        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        for (RequestMappingInfo requestMappingInfo : handlerMethods.keySet()) {
            Set<String> patterns = requestMappingInfo.getPatternsCondition().getPatterns();
            for (String pattern : patterns) {
                restApiPaths.add(pattern);
            }
        }

        return restApiPaths;
    }

    // 可以在此处进行进一步处理，例如打印输出或保存到文件等
    @PostConstruct
    public void printAllRestApiPaths() {
        List<String> restApiPaths = getAllRestApiPaths();
        for (String path : restApiPaths) {
            System.out.println(path);
        }
    }
}