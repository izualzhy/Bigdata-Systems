package cn.izualzhy.client;

import cn.izualzhy.pojo.ApiResponse;
import cn.izualzhy.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "user", url = "http://localhost:8211") // 声明为OpenFeign的客户端
public interface UserFacade {

   /**
    * 获取用户信息
    * @param id -- 用户编号
    * @return 用户信息
    */
   @GetMapping("/user/info/{id}")  // 注意方法和注解的对应选择
   public User getUser(@PathVariable("id") Long id);


   @GetMapping("/info/response/{id}")
   public User getUserInfoResponse(@PathVariable("id") Long id);
   /**
    * 修改用户信息
    * @param userInfo -- 用户
    * @return 用户信息
    */
   @PutMapping("/user/info") // 注意方法和注解的对应选择
   public User putUser(@RequestBody User userInfo);
}

