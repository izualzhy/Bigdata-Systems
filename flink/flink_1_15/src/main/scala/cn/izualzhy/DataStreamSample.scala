package cn.izualzhy

import org.apache.flink.streaming.api.scala._

object DataStreamSample extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  val text = env.fromCollection(
    List("a", "b", "c", "d", "e")
  )

  text.map(_.toUpperCase()).print()

  env.execute("DataStreamSample")
}
