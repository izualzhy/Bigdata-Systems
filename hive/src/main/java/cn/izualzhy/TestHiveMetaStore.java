package cn.izualzhy;

import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.thrift.TException;

import java.util.Arrays;
import java.util.List;

public class TestHiveMetaStore {

    public static HiveMetaStoreClient initHiveMetaStore(String hiveMetaStoreUri) throws MetaException {
        HiveConf hiveConf = new HiveConf();
        hiveConf.setVar(HiveConf.ConfVars.METASTOREURIS, hiveMetaStoreUri);
        hiveConf.set("_hive.hdfs.session.path", "");
        hiveConf.set("_hive.local.session.path", "");
        hiveConf.setVar(HiveConf.ConfVars.METASTORE_SCHEMA_VERIFICATION, "false");
        hiveConf.setVar(HiveConf.ConfVars.METASTORE_EXECUTE_SET_UGI, "false");
        hiveConf.setVar(HiveConf.ConfVars.METASTORE_USE_THRIFT_SASL, "false");

        return new HiveMetaStoreClient(hiveConf);
    }

    public static void test(HiveMetaStoreClient hiveMetaStoreClient) throws TException {
        System.out.println("getAllDatabases:");
        List<String> databases = hiveMetaStoreClient.getAllDatabases();
        databases.forEach(database -> {
            System.out.println("database : " + database);
            try {
                hiveMetaStoreClient.getAllTables(database).forEach(table -> {
                    System.out.println("\ttable : " + table);
                });
            } catch (MetaException e) {
                throw new RuntimeException(e);
            }
        });

        Table table = hiveMetaStoreClient.getTable("", "");
        System.out.println(table);
    }
    public static void main(String[] args) throws Exception {
        Arrays.stream(args).forEach(System.out::println);
        HiveMetaStoreClient hiveMetaStoreClient = initHiveMetaStore(args[0]);
        test(hiveMetaStoreClient);
    }
}
