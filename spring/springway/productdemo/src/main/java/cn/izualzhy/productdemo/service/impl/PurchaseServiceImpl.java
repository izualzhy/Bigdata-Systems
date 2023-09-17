package cn.izualzhy.productdemo.service.impl;

import cn.izualzhy.productdemo.dao.ProductDao;
import cn.izualzhy.productdemo.dao.PurchaseRecordDao;
import cn.izualzhy.productdemo.pojo.Product;
import cn.izualzhy.productdemo.pojo.PurchaseRecord;
import cn.izualzhy.productdemo.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PurchaseServiceImpl implements PurchaseService {
    @Autowired
    ProductDao productDao;
    @Autowired
    PurchaseRecordDao purchaseRecordDao;


    @Override
    @Transactional
    public boolean purchase(Long userId, Long productId, int quantity) {
        if (false) {
            // 可能造成库存<0
//        Product product = productDao.getProduct(productId);
            // 悲观锁 by for update
            Product product = productDao.getProductLock(productId);
            if (product == null || product.getStock() < quantity) {
                System.out.println("product:" + product);
                return false;
            }

            productDao.decreaseProduct(productId, quantity);

            PurchaseRecord purchaseRecord = initPurchaseRecord(userId, product, quantity);
            purchaseRecordDao.insertPurchaseRecord(purchaseRecord);
        } else {
            // 乐观锁 by version
            Product product = productDao.getProduct(productId);
            if (product == null || product.getStock() < quantity) {
                System.out.println("product:" + product);
                return false;
            }

            int version = product.getVersion();
            int result = productDao.decreaseProductByVersion(productId, quantity, version);
            if (result == 0) {
                return false;
            }

            PurchaseRecord purchaseRecord = initPurchaseRecord(userId, product, quantity);
            purchaseRecordDao.insertPurchaseRecord(purchaseRecord);
        }

        return true;
    }


    private PurchaseRecord initPurchaseRecord(Long userId, Product product, int quantity) {
        PurchaseRecord purchaseRecord = new PurchaseRecord();
        purchaseRecord.setNote("购买日志，时间：" + System.currentTimeMillis());
        purchaseRecord.setPrice(product.getPrice());
        purchaseRecord.setQuantity(quantity);
        purchaseRecord.setUserId(userId);
        purchaseRecord.setSum(product.getPrice() * quantity);
        purchaseRecord.setProductId(product.getId());

        return purchaseRecord;
    }

    @Override
    @Transactional
    public boolean transactionRollbackTest(Long productId) {
//        try {
            int row = productDao.insertProduct(productId);
            System.out.println("row:" + row);
            Product product = productDao.getProduct(productId);
            System.out.println("in transaction, product:" + product);

            row = productDao.insertProduct(productId);
            System.out.println("row:" + row);

            return true;
//        } catch (Exception e) {
//            System.out.println("e:" + e);
//            return false;
//        }
    }
}
