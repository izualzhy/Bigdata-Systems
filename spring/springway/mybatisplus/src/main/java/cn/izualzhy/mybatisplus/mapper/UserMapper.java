package cn.izualzhy.mybatisplus.mapper;

import cn.izualzhy.mybatisplus.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    List<User> foo(String name, Integer age);
}
