package cn.izualzhy

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.configuration.Configuration
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}
import org.slf4j.{Logger, LoggerFactory}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties

object FlinkOnK8STolerance extends App {
  val logger = LoggerFactory.getLogger("FlinkOnK8STolerance");
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

  val configuration = new Configuration()
  configuration.setString("uniqueId", args(0))
  env.getConfig.setGlobalJobParameters(configuration)

  val properties = new Properties()
  properties.setProperty("bootstrap.servers", args(2))
  val groupId = "zy_test_" + args(4)
  val sourceTopic = args(5)
  val sinkTopic = args(6)
  properties.setProperty("group.id", groupId)

  args.foreach(i => println(s"args:${i}"))

  val source = new FlinkKafkaConsumer[String](sourceTopic, new SimpleStringSchema(), properties).setStartFromLatest()
  val sink = new FlinkKafkaProducer[String](sinkTopic, new SimpleStringSchema(), properties)

  val sourceStream = env.addSource(source)
  val processStream = sourceStream.map(i => {
      // 构造异常情况: 字符串不带空格则报错
      val elems = i.split(" ")
      val subElems = elems(0).split("_")
      val length = subElems.map(_.length).sum
      elems(1) + "_" + length + elems(1).length
    }).map(i => i.toLowerCase)
    .map(new RichMapFunction[String, String] {
      private var uniqueId: String = null

      override def open(parameters: Configuration): Unit = {
        super.open(parameters)
        val globalParams = getRuntimeContext.getExecutionConfig.getGlobalJobParameters
        val globConf = globalParams.asInstanceOf[Configuration]
        uniqueId = globConf.getString("uniqueId", null)
      }

      override def map(value: String): String = {
        "FlinkOnK8STolerance-" + uniqueId + "-" + value
      }
    })

  processStream.addSink(new RichSinkFunction[String] {
    val logger: Logger = LoggerFactory.getLogger("RickSinkFunction")

    override def open(parameters: Configuration): Unit = {
    }

    override def invoke(value: String): Unit = {
      logger.info(s"sink to log:${value}")
      println(s"sink to stdout:${value}")
    }
  })

  processStream.addSink(sink)


  env.execute(this.getClass.getName)
}
