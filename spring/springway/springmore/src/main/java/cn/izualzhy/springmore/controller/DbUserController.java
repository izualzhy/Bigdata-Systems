package cn.izualzhy.springmore.controller;

import cn.izualzhy.springmore.pojo.DbUser;
import cn.izualzhy.springmore.service.JdbcTmplUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/dbuser")
public class DbUserController {
    //    @Autowired
    JdbcTmplUserService jdbcTmplUserService = null;

    DbUserController(JdbcTmplUserService jdbcTmplUserService) {
        this.jdbcTmplUserService = jdbcTmplUserService;
    }

    @PostMapping("/insert")
    @ResponseBody
    public DbUser insert(@RequestBody DbUser dbUser) {
        System.out.println("dbUser:" + dbUser);
        int updateCnt = jdbcTmplUserService.insertUser(dbUser);

        return dbUser;
    }
}
