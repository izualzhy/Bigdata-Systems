package cn.izualzhy.productdemo.dao;

import cn.izualzhy.productdemo.pojo.PurchaseRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PurchaseRecordDao {
    public int insertPurchaseRecord(PurchaseRecord purchaseRecord);
}
