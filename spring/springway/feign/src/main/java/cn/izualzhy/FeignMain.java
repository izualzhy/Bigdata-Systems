package cn.izualzhy;

import cn.izualzhy.client.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages="cn.izualzhy.client")
public class FeignMain {

   public static void main(String[] args) {
      SpringApplication.run(FeignMain.class, args);
   }
}
