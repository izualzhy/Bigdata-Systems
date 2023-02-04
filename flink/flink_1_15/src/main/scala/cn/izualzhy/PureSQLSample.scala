package cn.izualzhy

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

object PureSQLSample extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(1)
  val tEnv = StreamTableEnvironment.create(env)

  val source_table_ddl =
    """
      |CREATE TABLE orders (
      | order_number BIGINT,
      | price DECIMAL(32, 2),
      | buyer ROW<first_name STRING, last_name STRING>,
      | order_time TIMESTAMP(3)
      |) WITH (
      | 'connector' = 'datagen',
      | 'rows-per-second' = '1'
      |)""".stripMargin
  val sink_table_ddl =
    """
      |CREATE TABLE print_table WITH ('connector' = 'print')
      |LIKE orders (EXCLUDING ALL)""".stripMargin
  val insert_sql =
    """
      |INSERT INTO print_table SELECT * FROM orders""".stripMargin

  tEnv.executeSql(source_table_ddl)
  tEnv.executeSql(sink_table_ddl)
  tEnv.executeSql(insert_sql)
}
