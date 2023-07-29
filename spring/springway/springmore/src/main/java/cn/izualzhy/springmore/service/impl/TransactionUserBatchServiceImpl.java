package cn.izualzhy.springmore.service.impl;

import cn.izualzhy.springmore.pojo.TransactionUser;
import cn.izualzhy.springmore.service.TransactionUserBatchService;
import cn.izualzhy.springmore.service.TransactionUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionUserBatchServiceImpl implements TransactionUserBatchService {
    @Autowired
    TransactionUserService transactionUserService;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int insertUsers(List<TransactionUser> transactionUserList) {
        int count = 0;
        for (TransactionUser transactionUser : transactionUserList) {
            count += transactionUserService.insertUser(transactionUser);
        }
        return count;
    }
}
