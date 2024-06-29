package cn.izualzhy

import org.apache.flink.api.common.functions.JoinFunction
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.slf4j.LoggerFactory

object KafkaWindowJoinSample extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
  env.setParallelism(1)

  private val bootStrapServers = args(2)
  private val userActionTopic = args(3)
  private val featureTopic = args(4)
  val groupId = args(5)

  private val userActionStream = SourceUtils.generateKafkaUserActionStream(
    env, userActionTopic, bootStrapServers, groupId
  ).assignAscendingTimestamps(_._3)
  private val featureStream = SourceUtils.generateKafkaFeatureStream(
    env, featureTopic, bootStrapServers, groupId
  ).assignAscendingTimestamps(_._3)

  userActionStream.join(featureStream)
    .where(_._1)
    .equalTo(_._1)
    .window(TumblingEventTimeWindows.of(Time.seconds(5)))
    .apply(new JoinFunction[(String, String, Long), (String, String, Long), (String)] {
      override def join(in1: (String, String, Long), in2: (String, String, Long)): String = {
        "left : " + in1 + "\tright : " + in2
      }
    })
    .addSink(new RichSinkFunction[String] {
      val logger = LoggerFactory.getLogger("WindowJoinSample")
      override def invoke(value: String, context: SinkFunction.Context): Unit = {
        logger.info(s"invoke : ${value}")
      }
    })
    .setParallelism(1)

  env.execute("WindowJoinSample")
}
