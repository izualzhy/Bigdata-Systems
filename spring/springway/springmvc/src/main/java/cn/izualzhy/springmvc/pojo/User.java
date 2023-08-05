package cn.izualzhy.springmvc.pojo;

import lombok.Data;

@Data
public class User {
    public User(int id, String userName, String note) {
        this.id = id;
        this.userName = userName;
        this.note = note;
    }
    public int id;
    public String userName;
    public String note;
}
