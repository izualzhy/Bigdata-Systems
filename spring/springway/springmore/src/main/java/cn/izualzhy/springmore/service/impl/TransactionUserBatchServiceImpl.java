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
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public int insertUsers(List<TransactionUser> transactionUserList) {
        int count = 0;
        for (TransactionUser transactionUser : transactionUserList) {
            count += transactionUserService.insertUser(transactionUser);
        }
        return count;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public String transactionTest() {
        TransactionUser transactionUser1 = new TransactionUser();
        transactionUser1.setUserName("transaction_test_name_1");
        transactionUser1.setNote("transaction_test_note_1");
        transactionUserService.insertUser(transactionUser1);

        if (true) {
            throw new RuntimeException("test");
        }

        TransactionUser transactionUser2 = new TransactionUser();
        transactionUser2.setUserName("transaction_test_name_2");
        transactionUser2.setNote("transaction_test_note_2");
        transactionUserService.insertUser(transactionUser2);

        return "success";
    }
}
