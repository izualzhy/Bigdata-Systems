package cn.izualzhy.springmore.pojo;

import cn.izualzhy.springmore.enumeration.SexEnum;
import lombok.Data;


@Data
public class DbUser {
    private Long id = null;
    private String userName = null;
    private SexEnum sexEnum = null;
    private String note = null;
}
