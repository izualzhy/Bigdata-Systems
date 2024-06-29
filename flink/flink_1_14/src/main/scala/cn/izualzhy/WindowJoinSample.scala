package cn.izualzhy

import org.apache.flink.api.common.functions.JoinFunction
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time

object WindowJoinSample extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
  env.setParallelism(1)

  private val userActionStream =
    SourceUtils.generateLocalUserActionStream(env)
      .assignAscendingTimestamps(_._3)

  private val featureStream =
    SourceUtils.generateLocalFeatureStream(env)
      .assignAscendingTimestamps(_._3)

  featureStream.join(userActionStream)
    .where(_._1)
    .equalTo(_._1)
    .window(TumblingEventTimeWindows.of(Time.seconds(5)))
    .apply(new JoinFunction[(String, String, Long), (String, String, Long), (String)] {
      override def join(in1: (String, String, Long), in2: (String, String, Long)): String = {
        "left : " + in1 + "\tright : " + in2
      }
    })
    .print()
    .setParallelism(1)

  env.execute("WindowJoinSample")
}
