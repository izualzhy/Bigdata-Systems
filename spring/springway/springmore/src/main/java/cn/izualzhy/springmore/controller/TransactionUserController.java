package cn.izualzhy.springmore.controller;

import cn.izualzhy.springmore.pojo.TransactionUser;
import cn.izualzhy.springmore.service.TransactionUserBatchService;
import cn.izualzhy.springmore.service.TransactionUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/transaction")
public class TransactionUserController {
    @Autowired
    private TransactionUserService transactionUserService = null;
    @Autowired
    private TransactionUserBatchService transactionUserBatchService = null;

    @RequestMapping("/getUser")
    @ResponseBody
    public TransactionUser getUser(Long id) {
        return transactionUserService.getUser(id);
    }

    @RequestMapping("/insertUser")
    @ResponseBody
    public Map<String, Object> insertUser(String userName, String note) {
        TransactionUser transactionUser = new TransactionUser();
        transactionUser.setUserName(userName);
        transactionUser.setNote(note);

        int update = transactionUserService.insertUser(transactionUser);
        Map<String, Object> result = new HashMap<>();
        result.put("success", update == 1);
        result.put("user", transactionUser);

        return result;
    }

    @RequestMapping("/insertUsers")
    @ResponseBody
    public Map<String, Object> insertUsers(String userName1, String note1, String userName2,
                                           String note2) {
        TransactionUser user1 = new TransactionUser();
        user1.setUserName(userName1);
        user1.setNote(note1);
        TransactionUser user2 = new TransactionUser();
        user2.setUserName(userName2);
        user2.setNote(note2);
        List<TransactionUser> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        // 结果会回填主键，返回插入条数
        int inserts = transactionUserBatchService.insertUsers(userList);
        Map<String, Object> result = new HashMap<>();
        result.put("success", inserts>0);
        result.put("user", userList);
        return result;
    }

    @RequestMapping("/transactionTest")
    @ResponseBody
    public String transactionTest() {
        String result = transactionUserBatchService.transactionTest();
        return result;
    }

}
