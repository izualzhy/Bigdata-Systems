package cn.izualzhy.springmore.controller;

//import cn.izualzhy.springmore.mapper.MybatisUserMapper;
import cn.izualzhy.springmore.pojo.MybatisUser;
import cn.izualzhy.springmore.service.MyBatisUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/mybatis")
public class MyBatisController {
    @Autowired
    private MyBatisUserService myBatisUserService = null;
//    @Autowired
//    private MybatisUserMapper mybatisUserMapper = null;

    @RequestMapping("/getUser")
    @ResponseBody
    public MybatisUser getUser(Long id) {
        return myBatisUserService.getUser(id);
    }

//    @RequestMapping("/listUser")
//    @ResponseBody
//    public List<MybatisUser> listUser() {
//        return mybatisUserMapper.selectList(null);
//    }

}
