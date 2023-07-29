package cn.izualzhy.springmore.dao;

import cn.izualzhy.springmore.pojo.MybatisUser;
import org.springframework.stereotype.Repository;

@Repository
public interface MyBatisUserDao {
    public MybatisUser getUser(Long id);
}
