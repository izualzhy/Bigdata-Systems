package cn.izualzhy.springmore.service.impl;

import cn.izualzhy.springmore.enumeration.SexEnum;
import cn.izualzhy.springmore.pojo.DbUser;
import cn.izualzhy.springmore.service.JdbcTmplUserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.List;

@Service
public class JdbcTmplUserServiceImpl implements JdbcTmplUserService {
//    @Autowired
    private JdbcTemplate jdbcTemplate = null;

    JdbcTmplUserServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private RowMapper<DbUser> getUserMapper() {
        RowMapper<DbUser> userRowMapper = (ResultSet rs, int rowNum) -> {
            DbUser dbUser = new DbUser();
            dbUser.setId(rs.getLong("id"));
            dbUser.setUserName(rs.getString("user_name"));

            int sexId = rs.getInt("sex");
            SexEnum sexEnum = SexEnum.getEnumById(sexId);
            dbUser.setSexEnum(sexEnum);
            dbUser.setNote(rs.getString("note"));

            return dbUser;
        };

        return userRowMapper;
    }

    @Override
    public DbUser getUser(Long id) {
        String sql = "select id, user_name, sex, note from t_user where id = ?";
        Object[] params = new Object[]{id};

        DbUser dbUser = jdbcTemplate.queryForObject(sql, params, getUserMapper());
        return dbUser;
    }

    @Override
    public List<DbUser> findUsers(String userName, String note) {
        String sql = "select id, suer_name, sex, note from t_user where user_name like concat('%, ?, '%') " +
                " and note like concat('%', ?, '%')";
        Object[] params = new Object[]{userName, note};

        List<DbUser> userList = jdbcTemplate.query(sql, params, getUserMapper());
        return userList;
    }

    @Override
    public int insertUser(DbUser dbUser) {
        String sql = "insert into t_user (user_name, sex, note) values (?, ?, ?)";
        return jdbcTemplate.update(sql, dbUser.getUserName(), dbUser.getSexEnum().getId(), dbUser.getNote());
    }

    @Override
    public int updateUser(DbUser dbUser) {
        String sql = "insert into t_user (user_name, sex, note) values (?, ?, ?) where id = ?";
        return jdbcTemplate.update(sql, dbUser.getUserName(), dbUser.getSexEnum().getId(), dbUser.getNote(), dbUser.getId());
    }

    @Override
    public int deleteUser(Long id) {
        String sql = "delete from t_user where id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
