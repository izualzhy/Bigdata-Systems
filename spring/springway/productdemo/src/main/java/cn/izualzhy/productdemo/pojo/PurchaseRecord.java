package cn.izualzhy.productdemo.pojo;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;

@Alias("purchaseRecord")
@Data
public class PurchaseRecord {
    private Long id;
    private Long userId;
    private Long productId;
    private double price;
    private int quantity;
    private double sum;
    private Timestamp purchaseTime;
    private String note;
}
