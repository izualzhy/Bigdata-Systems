package cn.izualzhy;

import org.apache.hive.jdbc.HiveConnection;
import org.apache.hive.jdbc.HiveStatement;
import org.apache.hive.service.rpc.thrift.TSessionHandle;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;

public class HiveJDBCSample {

    static HiveConnection initConnection(String url, String queueName) throws Exception {
        Class.forName("org.apache.hive.jdbc.HiveDriver");

        Properties properties = new Properties();
        properties.setProperty("user", "hive");
        properties.setProperty("password", "");
        properties.setProperty("hiveconf:mapreduce.job.queuename", queueName);
//        properties.setProperty("hiveconf:hive.execution.engine", "spark")
//        properties.setProperty("hiveconf:hive.exec.parallel", "TRUE")
        properties.setProperty("hiveconf:hive.exec.pre.hooks", "");

        HiveConnection connection = (HiveConnection) DriverManager.getConnection(url, properties);
        System.out.println(connection.getConnectedUrl());
        System.out.println(connection.getProtocol());

        Field sessConfMapField = connection.getClass().getDeclaredField("sessHandle");
        sessConfMapField.setAccessible(true);
        TSessionHandle sessionHandle = (TSessionHandle) sessConfMapField.get(connection);
        System.out.println(sessionHandle);

        return connection;
    }

    static void execute(HiveStatement hiveStatement, String sql) throws SQLException {
        boolean hasResult = hiveStatement.execute(sql);
        System.out.println("hasResult:" + hasResult);
        if (hasResult) {
            ResultSet resultSet = hiveStatement.getResultSet();
            System.out.println("resultSet:" + resultSet);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            System.out.println("------ getColumnName ------");
            for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                System.out.println(resultSetMetaData.getColumnName(i + 1));
            }

            System.out.println("------ getColumnLabel ------");
            for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                System.out.println(resultSetMetaData.getColumnLabel(i + 1));
            }

            System.out.println("------ getColumnLabel ------");
            for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                System.out.println(resultSetMetaData.getColumnType(i + 1));
                System.out.println(resultSetMetaData.getColumnTypeName(i + 1));
            }

//            System.out.println("------ getTableName ------");
//            for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
//                System.out.println(resultSetMetaData.getTableName(i + 1));
//            }

//            System.out.println("------ getSchemaName ------");
//            for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
//                System.out.println(resultSetMetaData.getSchemaName(i));
//            }

            while (resultSet.next()) {
                String[] values = new String[resultSetMetaData.getColumnCount()];
                for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                    values[i] = resultSet.getString(i + 1);
                    if (values[i] == null) {
                        System.out.println("exist null string.");
                    }
                }
                System.out.println("|" + String.join("\t", values) + "|");
            }

            resultSet.close();
        } else {
        }
    }

    public static void main(String[] args) throws Exception {
        HiveConnection hiveConnection = null;
        HiveStatement hiveStatement = null;
        try {
            hiveConnection = initConnection(args[0], args[1]);
            hiveStatement = (HiveStatement) hiveConnection.createStatement();

            execute(hiveStatement, "set spark.app.name=test#test#ufo");
            if (args.length >= 3) {
                Path path = Paths.get(args[2]);
                byte[] bytes = Files.readAllBytes(path);
                String sql = new String(bytes, StandardCharsets.UTF_8);

                execute(hiveStatement, "set hive.exec.post.hooks=org.apache.hadoop.hive.ql.hooks.LineageLogger");
                execute(hiveStatement, sql);
            } else {
                execute(hiveStatement, "select 'hello ', 1");
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            hiveStatement.close();
            hiveConnection.close();
        }
    }
}
