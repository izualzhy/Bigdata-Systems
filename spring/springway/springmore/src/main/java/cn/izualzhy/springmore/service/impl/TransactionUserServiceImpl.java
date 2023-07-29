package cn.izualzhy.springmore.service.impl;

import cn.izualzhy.springmore.dao.TransactionUserDao;
import cn.izualzhy.springmore.pojo.TransactionUser;
import cn.izualzhy.springmore.service.TransactionUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionUserServiceImpl implements TransactionUserService {
    @Autowired
    private TransactionUserDao transactionUserDao = null;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 1)
    public int insertUser(TransactionUser transactionUser) {
        return transactionUserDao.insertUser(transactionUser);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 1)
    public TransactionUser getUser(Long id) {
        return transactionUserDao.getUser(id);
    }

}
