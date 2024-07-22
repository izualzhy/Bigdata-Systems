package cn.izualzhy

import cn.izualzhy.FlinkOnK8SBenchmark.env
import cn.izualzhy.FlinkOnK8STest.args
import org.apache.flink.runtime.state.memory.MemoryStateBackend
import org.apache.flink.streaming.api.scala._

import java.time.LocalDateTime
import java.util.Date
import java.util.concurrent.TimeUnit

object TestBatchStream extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setStateBackend(new MemoryStateBackend())
  val checkpointConfig = env.getCheckpointConfig
  checkpointConfig.setCheckpointInterval(60000)

  args.foreach(i => println(s"args:${i}"))

  val cnt = if (args(0) == "--properties-file") {
    args(2).toInt
  } else {
    args(0).toInt
  }

  env.fromSequence(1, cnt)
    .map(i => {
      TimeUnit.SECONDS.sleep(60)
      LocalDateTime.now() + " " + i.toString
    }).print(this.getClass.getName + " >>>>>> ")

  env.execute()
}
