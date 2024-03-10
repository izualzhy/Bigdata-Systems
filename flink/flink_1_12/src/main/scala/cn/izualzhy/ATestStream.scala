package cn.izualzhy

import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

object ATestStream extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment

  private val threshold = 5

  env.fromSequence(1, 10)
    .filter(_ > threshold)
    .print("after filter:")

  env.execute()
}
