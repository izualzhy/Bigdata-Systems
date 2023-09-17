package cn.izualzhy.productdemo.pojo;


import lombok.Data;
import org.apache.ibatis.type.Alias;

@Alias("product")
@Data
public class Product {
    private Long id;
    private String productName;
    private int stock;
    private double price;
    private int version;
    private String note;
}
