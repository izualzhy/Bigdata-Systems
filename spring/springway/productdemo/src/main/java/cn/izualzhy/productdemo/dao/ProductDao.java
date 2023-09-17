package cn.izualzhy.productdemo.dao;

import cn.izualzhy.productdemo.pojo.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductDao {
    public Product getProduct(Long id);
    public Product getProductLock(Long id);
    public int decreaseProduct(Long id, int quantity);
    public int decreaseProductByVersion(Long id, int quantity, int version);
    public int insertProduct(Long id);
}
