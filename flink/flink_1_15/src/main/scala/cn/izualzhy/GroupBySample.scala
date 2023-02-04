package cn.izualzhy

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

object GroupBySample extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(1)
  val tEnv = StreamTableEnvironment.create(env)

  val words = List("Hello World", "Hello Scala")
  val inputTable = tEnv.fromDataStream(
    env.fromCollection(words)
      .flatMap(_.toList))
  tEnv.createTemporaryView("input_table", inputTable)
}
