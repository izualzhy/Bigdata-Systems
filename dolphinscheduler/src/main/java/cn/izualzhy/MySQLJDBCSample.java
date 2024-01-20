package cn.izualzhy;

import java.io.PrintWriter;
import java.sql.*;
import java.util.Collections;

public class MySQLJDBCSample {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/quartz_jobs?serverTimezone=Asia/Shanghai";
        String user = "izualzhy";
        String passwd = "izualzhy_test";
        DriverManager.setLogWriter(new PrintWriter(System.out));

        try (Connection connection = DriverManager.getConnection(url, user, passwd)) {
            try (Statement statement = connection.createStatement()) {
                String sql = "SHOW TABLES";
                try (ResultSet resultSet = statement.executeQuery(sql)) {
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        System.out.printf("%-32s\t", resultSet.getMetaData().getColumnName(i));
                    }

                    System.out.println("\n" + String.join("", Collections.nCopies(32, "-")));

                    while (resultSet.next()) {
                        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                            System.out.printf("%-32s\t", resultSet.getString(i));
                        }
                        System.out.println();
                    }
                }
            }
        }
    }
}
