package cn.izualzhy.springmore.service.impl;

import cn.izualzhy.springmore.dao.MyBatisUserDao;
import cn.izualzhy.springmore.pojo.MybatisUser;
import cn.izualzhy.springmore.service.MyBatisUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyBatisUserServiceImpl implements MyBatisUserService {
    @Autowired
    private MyBatisUserDao myBatisUserDao = null;

    @Override
    public MybatisUser getUser(Long id) {
        return myBatisUserDao.getUser(id);
    }
}
