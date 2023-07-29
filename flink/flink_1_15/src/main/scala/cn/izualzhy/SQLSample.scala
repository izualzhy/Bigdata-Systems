package cn.izualzhy

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

object SQLSample extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(1)
  val tEnv = StreamTableEnvironment.create(env)

  val words = List(
    "a=1,b=2,name=hello",
    "a=1,b=2,name=$hello",
    "a=1,b=2,name=world",
    "a=1,b=2,name=$world"
  )
  val inputTable = tEnv.fromDataStream(
    env.fromCollection(words)
    //      .flatMap(_.toList)
  )
  tEnv.createTemporaryView("input_table", inputTable)
  inputTable.printSchema()

  val map_table = tEnv.sqlQuery(
    """
      |SELECT STR_TO_MAP(`f0`) AS kv FROM input_table""".stripMargin
  )
  tEnv.createTemporaryView("map_table", map_table)

  val outputTable = tEnv.sqlQuery(
    """
      |SELECT * FROM map_table
      |WHERE kv['name'] IN ('$hello', 'hello') """.stripMargin
  )
  tEnv.toDataStream(outputTable).print()

  env.execute()
}
