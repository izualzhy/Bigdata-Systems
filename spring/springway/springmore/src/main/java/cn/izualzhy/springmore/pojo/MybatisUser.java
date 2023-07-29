package cn.izualzhy.springmore.pojo;

import cn.izualzhy.springmore.enumeration.SexEnum;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Alias(value = "mybatisUser")
@Data
public class MybatisUser {
    private Long id = null;
    private String userName = null;
    private String note = null;
    private SexEnum sex = null;

    public MybatisUser() {
    }
}
