package cn.izualzhy.springmore.service;

import cn.izualzhy.springmore.pojo.DbUser;

import java.util.List;

public interface JdbcTmplUserService {
    public DbUser getUser(Long id);

    public List<DbUser> findUsers(String userName, String note);

    public int insertUser(DbUser dbUser);

    public int updateUser(DbUser dbUser);

    public int deleteUser(Long id);
}
