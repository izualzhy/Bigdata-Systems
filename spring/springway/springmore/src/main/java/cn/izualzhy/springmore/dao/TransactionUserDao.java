package cn.izualzhy.springmore.dao;

import cn.izualzhy.springmore.pojo.TransactionUser;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionUserDao {
    TransactionUser getUser(Long id);
    int insertUser(TransactionUser transactionUser);
}
