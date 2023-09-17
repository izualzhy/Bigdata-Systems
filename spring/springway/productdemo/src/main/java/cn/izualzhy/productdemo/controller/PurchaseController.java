package cn.izualzhy.productdemo.controller;

import cn.izualzhy.productdemo.service.PurchaseService;
import cn.izualzhy.productdemo.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * 购买
 * 数据库初始化: INSERT INTO T_Product (product_name, stock, price, version, note) VALUES ('Product A', 100, 10.99, 1, 'Note A');
 * @author zhangying
 * @date 2023/08/31
 */
@RestController
public class PurchaseController {
    @Autowired
    PurchaseService purchaseService;

    @GetMapping("/test")
    public ModelAndView testPage() {
        ModelAndView mv = new ModelAndView("test");
        return mv;
    }

    @GetMapping("/testBatch")
    public ModelAndView testBatchPage() {
        ModelAndView mv = new ModelAndView("test_batch");
        return mv;
    }

    @PostMapping("/purchase")
    @ResponseBody
    public Result purchase(Long userId, Long productId, int quantity) {
        boolean success = purchaseService.purchase(userId, productId, quantity);

        return new Result(success, success ? "success" : "fail");
    }

    @PostMapping("/transaction_rollback_test")
    @ResponseBody
    public Result transactionRollback(Long productId) {
        System.out.println("productId:" + productId);
        boolean success = purchaseService.transactionRollbackTest(productId);

        return new Result(success, success ? "success" : "fail");
    }
}
