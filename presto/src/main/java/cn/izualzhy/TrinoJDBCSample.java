package cn.izualzhy;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.Properties;

public class TrinoJDBCSample {
    static HikariDataSource hikariDataSource;
    static Connection initConnection(String connectionUrl) throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", "udaplus");
        properties.setProperty("SSL", "false");

        System.out.println("connectionUrl = " + connectionUrl);
        Connection connection = DriverManager.getConnection(connectionUrl, properties);
        return connection;
    }

    static void initHikariConnection(String connectionUrl) {
        hikariDataSource = new HikariDataSource();
//        hikariDataSource.setDriverClassName("com.facebook.presto.jdbc.PrestoDriver");
        hikariDataSource.setDriverClassName("io.trino.jdbc.TrinoDriver");
        hikariDataSource.setJdbcUrl(connectionUrl);
//        hikariDataSource.addDataSourceProperty("user", "test");
    }


    public static void main(String[] args) throws SQLException {
        test(args);
        testHikari(args);
    }

    private static void test(String[] args) throws SQLException {
        try (Connection connection = initConnection(args[0])){
            try (Statement statement = connection.createStatement()){
                try (ResultSet resultSet = statement.executeQuery("SELECT 123")) {
                    while (resultSet.next()) {
                        System.out.println("result = " + resultSet.getInt(1));
                    }
                }
            }
        }
    }

    private static void testHikari(String[] args) throws SQLException {
        initHikariConnection(args[0]);
        try (Connection connection = (hikariDataSource.getConnection())){
            try (Statement statement = connection.createStatement()){
                try (ResultSet resultSet = statement.executeQuery("SELECT 123")) {
                    while (resultSet.next()) {
                        System.out.println("result = " + resultSet.getInt(1));
                    }
                }
            }
        }

//        JdbcTemplate jdbcTemplate = new JdbcTemplate(hikariDataSource);
//        jdbcTemplate.execute("SELECT 123456");
    }

}
