package cn.izualzhy

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}
import org.slf4j.LoggerFactory

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties

object FlinkOnK8SBenchmark extends App {
  val logger = LoggerFactory.getLogger("FlinkOnK8SBenchmark");
  val env = StreamExecutionEnvironment.getExecutionEnvironment;

  args.foreach(s => logger.info(s"arg : ${s}"))
  args.foreach(s => println(s"arg : ${s}"))

  val currentDate = LocalDateTime.now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss"));

  val fsStateBackend = new FsStateBackend(args(7))
  env.setStateBackend(fsStateBackend);
  val checkpointConfig = env.getCheckpointConfig
  checkpointConfig.setCheckpointInterval(args(3).toInt)
  checkpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
  checkpointConfig.setCheckpointTimeout(30000)
  checkpointConfig.setTolerableCheckpointFailureNumber(5)

  val properties = new Properties()
  properties.setProperty("bootstrap.servers", args(2))
  val groupId = "zy_test_" + args(4)
  val sourceTopic = args(5)
  val sinkTopic = args(6)
  properties.setProperty("group.id", groupId)

  args.foreach(i => println(s"args:${i}"))

  val source = new FlinkKafkaConsumer[String](sourceTopic, new SimpleStringSchema(), properties).setStartFromEarliest()
  val sink = new FlinkKafkaProducer[String](sinkTopic, new SimpleStringSchema(), properties)

  val sourceStream = env.addSource(source)
  sourceStream.map(i => {
      val elems = i.split(" ")
      val subElems = elems(0).split("_")
      val length = subElems.map(_.length).sum + elems(1).length
      val isTel = i.matches("1[38]\\d{9}")
      val isEmail = i.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")
      val isIDCard = i.matches("\"\\\\d{17}[\\\\d|x|X]\"")
      val a = i.matches(" \"[a-zA-Z0-9]+@[a-zA-Z0-9]+\\\\.[a-zA-Z]+\"")
      "(" + length + " " + isTel + " " + isEmail + " " + isIDCard + " " + a + ")"
    }).map(i => i.toString.toUpperCase())
    .addSink(sink)

  env.execute(this.getClass.getName)


}
