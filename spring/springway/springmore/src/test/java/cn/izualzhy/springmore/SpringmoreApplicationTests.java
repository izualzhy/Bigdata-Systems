package cn.izualzhy.springmore;

//import cn.izualzhy.springmore.mapper.MybatisUserMapper;
import cn.izualzhy.springmore.pojo.MybatisUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SpringmoreApplicationTests {

//    @Autowired
//    private MybatisUserMapper mybatisUserMapper = null;

    @Test
    void contextLoads() {
    }

    @Test
    public void testSelect() {
//        List<MybatisUser> mybatisUserList = mybatisUserMapper.selectList(null);
//        mybatisUserList.forEach(System.out::println);
    }

}
