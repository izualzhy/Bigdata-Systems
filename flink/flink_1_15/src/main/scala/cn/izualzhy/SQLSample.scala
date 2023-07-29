package cn.izualzhy

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

object SQLSample extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(1)
  val tEnv = StreamTableEnvironment.create(env)

  val words = List("Hello World", "Hello Scala")
  val inputTable = tEnv.fromDataStream(
    env.fromCollection(words)
      .flatMap(_.toList))
  tEnv.createTemporaryView("input_table", inputTable)

  val outputTable = tEnv.sqlQuery(
    """
      |SELECT DATE_FORMAT(FROM_UNIXTIME(1655273888), 'yyyy-MM-dd HH:00'), count(*) AS pv
      |FROM input_table
      |GROUP BY DATE_FORMAT(FROM_UNIXTIME(1655273888), 'yyyy-MM-dd HH:00')""".stripMargin)
//  tEnv.toDataStream(outputTable).print()
  tEnv.toChangelogStream(outputTable).print()

  env.execute()
}
