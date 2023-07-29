package cn.izualzhy;

import org.apache.hive.jdbc.HiveConnection;
import org.apache.hive.jdbc.HiveDatabaseMetaData;
import org.apache.hive.jdbc.HiveStatement;
import org.apache.hive.service.rpc.thrift.TCLIService;
import org.apache.hive.service.rpc.thrift.TSessionHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class HiveJDBCSample {

    static HiveConnection initConnection(String url, String queueName) throws Exception {
        Class.forName("org.apache.hive.jdbc.HiveDriver");

        Properties properties = new Properties();
        properties.setProperty("user", "hive");
        properties.setProperty("password", "");
        properties.setProperty("hiveconf:mapreduce.job.queuename", queueName);
        properties.setProperty("hive.execution.engine", "spark");
        properties.setProperty("hive.exec.pre.hooks", "");

        java.util.Enumeration<Driver> drivers = DriverManager.getDrivers();
        Driver kyuubiDriver = null;
        while (drivers.hasMoreElements()) {
            Driver registerDriver = drivers.nextElement();
            System.out.println("driver:" + registerDriver);
            if (registerDriver.getClass().getName().contains("kyuubi")) {
                kyuubiDriver = registerDriver;
            }
        }
        DriverManager.deregisterDriver(kyuubiDriver);
        Driver driver = DriverManager.getDriver(url);
        System.out.println("url:" + url + " driver:" + driver);
        drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver registerDriver = drivers.nextElement();
            System.out.println("driver:" + registerDriver);
        }

        DriverManager.setLoginTimeout(1800);
        HiveConnection connection = (HiveConnection) DriverManager.getConnection(url, properties);
        System.out.println(connection.getConnectedUrl());
        System.out.println(connection.getProtocol());
        System.out.println(connection.getProtocol());

        Field clientField = connection.getClass().getDeclaredField("client");
        clientField.setAccessible(true);
        TCLIService.Iface client = (TCLIService.Iface) clientField.get(connection);

        Field sessConfMapField = connection.getClass().getDeclaredField("sessHandle");
        sessConfMapField.setAccessible(true);
        TSessionHandle sessionHandle = (TSessionHandle) sessConfMapField.get(connection);
        System.out.println(sessionHandle);

        HiveDatabaseMetaData hiveDatabaseMetaData = new HiveDatabaseMetaData(connection, client, sessionHandle);
        System.out.println("databaseProductName : " + hiveDatabaseMetaData.getDatabaseProductName());
        System.out.println("databaseProductVersion : " + hiveDatabaseMetaData.getDatabaseProductVersion());
        System.out.println("databaseMajorVersion : " + hiveDatabaseMetaData.getDatabaseMajorVersion());
        System.out.println("databaseMinorVersion : " + hiveDatabaseMetaData.getDatabaseMinorVersion());

        Field loginTimeoutField = connection.getClass().getDeclaredField("loginTimeout");
        loginTimeoutField.setAccessible(true);
//        loginTimeoutField.set(connection, 1800);
        int loginTimeout = (int) loginTimeoutField.get(connection);
        System.out.println("loginTimeout:" + loginTimeout);

        boolean valid = connection.isValid(1000);
        System.out.println("valid:" + valid);

        return connection;
    }

    static void execute(HiveStatement hiveStatement, String sql) throws SQLException, InterruptedException {
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
                System.out.println("wait for a moment");
                TimeUnit.SECONDS.sleep(10);
            }

            System.out.println("query data ready.");

            resultSet.close();
        } else {
        }
    }

    public static void main(String[] args) throws Exception {
        Logger logger = LoggerFactory.getLogger(HiveLogger.class);

        HiveConnection hiveConnection = null;
        HiveStatement hiveStatement = null;
        try {
            hiveConnection = initConnection(args[0], args[1]);

//            hiveStatement = (HiveStatement) hiveConnection.createStatement();
//            HiveLogger hiveLogger = new HiveLogger(hiveStatement);
//            hiveLogger.start();
//            execute(hiveStatement, "set spark.app.name=ufo@player");
//            execute(hiveStatement, "select * from dataware.dim_person_group_relation_lpc where dt='20230308' limit 1");
//            execute(hiveStatement, "-- set hive.execution.engine=mr");
//            execute(hiveStatement, "select dt, count(1) from  default.zbk_tmp_d_lxj_01_linshi group by dt");
        } catch (Exception e) {
            logger.error("exception in main", e);
        } finally {
            if (hiveConnection != null) {
                hiveConnection.close();
            }
            if (hiveStatement != null) {
                hiveStatement.close();
            }
        }

        logger.info("quit main.");
    }

    static class HiveLogger extends Thread {
        private HiveStatement hiveStatement;
        private Logger logger = LoggerFactory.getLogger(HiveLogger.class);
        HiveLogger(HiveStatement statement) throws SQLException {
            try {
                hiveStatement = statement;
            } catch (Exception e) {
                logger.error("unwrap to HivePreparedStatement failed exception:", e);
                throw e;
            }
        }

        @Override
        public void run() {
            try {
                logger.debug("HiveLogger run, closed:{} hasMoreLogs:{}",
                        hiveStatement.isClosed(), hiveStatement.hasMoreLogs());
                while (!hiveStatement.isClosed() && hiveStatement.hasMoreLogs()) {
                    try {
                        List<String> logs = hiveStatement.getQueryLog();
                        String batchLogs = String.join("\n\t", logs);
                        if (!logs.isEmpty()) {
                            logger.info("\n\t{}", batchLogs);
                        }
                        Thread.sleep(1000L);
                    } catch (SQLException e) {
                        logger.warn("Log SQLException:{}", e.toString());
                        return;
                    } catch (InterruptedException e) {
                        logger.warn("Log InterruptedException:{}", e.toString());
                        showRemainingLogsIfAny();
                        return;
                    }
                }
            } catch (Exception e) {
                logger.info("HiveLogger Exception", e);
                e.printStackTrace();
            }

            showRemainingLogsIfAny();
        }

        private void showRemainingLogsIfAny() {
            List<String> logs = null;
            do {
                try {
                    logs = hiveStatement.getQueryLog();
                } catch (SQLException e) {
                    logger.warn("Log SQLException:{}", e.toString());
                    return;
                }

                String batchLogs = String.join("\n\t", logs);
                if (!logs.isEmpty()) {
                    logger.info("\n\t{}", batchLogs);
                }
            } while (logs.size() > 0);
        }
    }
}
