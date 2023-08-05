package cn.izualzhy.springmore.service;

import cn.izualzhy.springmore.pojo.TransactionUser;

import java.util.List;

public interface TransactionUserBatchService {
    public int insertUsers(List<TransactionUser> transactionUserList);
    public String transactionTest();
}
