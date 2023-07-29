package cn.izualzhy.mybatisplus;

import cn.izualzhy.mybatisplus.mapper.UserMapper;
import cn.izualzhy.mybatisplus.pojo.User;
import org.apache.ibatis.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MybatisplusApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private UserMapper userMapper;

	@Test
	public void testSelect() {
//		for (int i = 10; i < 20; i++) {
//			User user = new User();
//			user.setAge(i);
//			user.setName("name_" + i);
//			user.setEmail(i + "@163.com");
//			userMapper.insert(user);
//		}
		System.out.println("------------------ list begin");
		List<User> userList = userMapper.selectList(null);
		userList.forEach(System.out::println);
		System.out.println("------------------ list finish");

		System.out.println("------------------ select begin");
		User user = userMapper.selectById(1);
		System.out.println(user);
		System.out.println("------------------ select finish");

		System.out.println("------------------ foo begin");
		userList = userMapper.foo("name", 5);
		System.out.println(userList.size());
		userList.forEach(System.out::println);
		System.out.println("------------------ foo finish");

	}

	@Test
	public void testFoo() {
		Logger logger = LoggerFactory.getLogger(UserMapper.class);

		// 获取MyBatis的SQL执行日志
//		LogFactory.useLog4JLogging();
		org.apache.ibatis.logging.Log log = LogFactory.getLog(UserMapper.class);

		// 执行查询
		List<User> users = userMapper.foo("test", 18);

		// 输出MyBatis执行的SQL语句
		if (log.isDebugEnabled()) {
			String sql = log.isDebugEnabled() ? log.toString().replace("org.apache.ibatis.logging.", "") : "";
			logger.debug("MyBatis SQL: {}", sql);
		}

		// 其他逻辑处理
		// ...
	}

}
