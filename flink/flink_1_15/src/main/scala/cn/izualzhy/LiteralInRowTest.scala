package cn.izualzhy

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

object LiteralInRowTest extends App {
  val env = StreamExecutionEnvironment.createLocalEnvironment(1)
  val tEnv = StreamTableEnvironment.create(env)

  val source = env.fromElements("test_f0_value")
  val source_table = tEnv.fromDataStream(source).as("f0")

  tEnv.createTemporaryView(
    "source_table",
    source_table)
  val sql = "SELECT Row(f0, 'literal_column') FROM source_table"
//  val sql = "SELECT Row('literal_column', f0) FROM source_table"
  tEnv.executeSql(sql).print()
}
