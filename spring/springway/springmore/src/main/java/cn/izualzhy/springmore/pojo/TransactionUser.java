package cn.izualzhy.springmore.pojo;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Alias("transaction_user")
@Data
public class TransactionUser {
    private Long id;
    private String userName;
    private String note;
}
