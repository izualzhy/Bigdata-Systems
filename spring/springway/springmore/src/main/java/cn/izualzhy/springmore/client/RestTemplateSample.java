package cn.izualzhy.springmore.client;

import cn.izualzhy.springmore.pojo.User;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class RestTemplateSample {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        List<Long> idList = new ArrayList<>();
//        idList.add(-1L);
//        idList.add(0L);
        idList.add(1L);

        for (Long id : idList) {
            try {
                User user = restTemplate.getForObject(
                        "http://127.0.0.1:8016/test/user?id={id}",
                        User.class,
                        id);
                System.out.println("user : " + user);
            } catch (Exception e) {
                System.out.println("e : " + e);
            }
        }
    }
}
