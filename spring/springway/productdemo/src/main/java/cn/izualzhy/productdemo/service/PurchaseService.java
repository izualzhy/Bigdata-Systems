package cn.izualzhy.productdemo.service;

public interface PurchaseService {
    public boolean purchase(Long userId, Long productId, int quantity);
    public boolean transactionRollbackTest(Long productId);
}
