package cn.izualzhy.springmore.service;

import cn.izualzhy.springmore.pojo.TransactionUser;

import java.util.List;

public interface TransactionUserService {
    public TransactionUser getUser(Long id);
    public int insertUser(TransactionUser transactionUser);
}
