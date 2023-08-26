package cn.izualzhy.springmore.pojo;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String userName;
    private String note;
    public User() {}

    public User(Long i, String a, String b) {
        this.id = i;
        this.userName = a;
        this.note = b;
    }
}
