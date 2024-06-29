package cn.izualzhy

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.slf4j.{Logger, LoggerFactory}

import java.util.Properties
import scala.util._

object SourceUtils {

  def generateLocalUserActionStream(env: StreamExecutionEnvironment): DataStream[(String, String, Long)] = {
    env.fromElements(
      ("a", "CLICK", 1 * 1000L),
      ("b", "CART", 2 * 1000L),
      ("a", "COMMENT", 3 * 1000L),
      ("b", "PAY", 4 * 1000L),
      ("b", "PLAY_FINISH", 96 * 1000L)
    )
  }

  def generateLocalFeatureStream(env: StreamExecutionEnvironment): DataStream[(String, String, Long)] = {
    env.fromElements(
      ("a", "feature_a", 1 * 1000L),
      ("b", "feature_b", 2 * 1000L),
    )
  }

  def generateKafkaUserActionStream(env: StreamExecutionEnvironment, topic: String, bootstrapServers: String, groupId: String): DataStream[(String, String, Long)] = {
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", bootstrapServers)
    properties.setProperty("group.id", groupId)

    val consumer = new FlinkKafkaConsumer[String](topic, new SimpleStringSchema(), properties).setStartFromLatest()

    env.addSource(consumer)
      .map(i => Try {
        val elems = i.split(" ")
        (elems(0), elems(1), elems(2).toLong * 1000)
      } match {
        case Success(value) => value
        case Failure(_) => ("UNKNOWN_USER_ID", "UNKNOWN_ACTION", 1L)
      })
  }

  def generateKafkaFeatureStream(env: StreamExecutionEnvironment, topic: String, bootstrapServers: String, groupId: String): DataStream[(String, String, Long)] = {
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", bootstrapServers)
    properties.setProperty("group.id", groupId)

    val consumer = new FlinkKafkaConsumer[String](topic, new SimpleStringSchema(), properties).setStartFromLatest()

    env.addSource(consumer)
      .map(i => Try {
        val elems = i.split(" ")
        (elems(0), elems(1), elems(2).toLong * 1000)
      } match {
        case Success(value) => value
        case Failure(_) => ("UNKNOWN_USER_ID", "UNKNOWN_FEATURE", 1L)
      })
  }

  def generateLocalSensorReadingStream(env: StreamExecutionEnvironment): DataStream[SensorReading] = {
    env.addSource(new SourceFunction[SensorReading] {
      val logger: Logger = LoggerFactory.getLogger(this.getClass)
      var currentIndex = 1
      var isRunning = true

      override def run(ctx: SourceFunction.SourceContext[SensorReading]): Unit = {
        while (isRunning) {
          val sensorId = if (currentIndex % 2 == 1) "sensor_a" else "sensor_b"
          val sensor = SensorReading(sensorId, currentIndex / 2, currentIndex * 10000L)

          logger.info("source collect sensor:{}", sensor)

          ctx.collect(sensor)

          currentIndex += 1
          Thread.sleep(10000)
        }
      }

      override def cancel(): Unit = {
        isRunning = false
      }
    })

//    env.fromElements(
//      SensorReading("sensor_a", 1, 1000L),
//      SensorReading("sensor_b", 1, 1500L),
//      SensorReading("sensor_a", 2, 2000L),
//      SensorReading("sensor_a", 3, 2500L),
//    )
  }

  def generateKafkaSensorReadingStream(env: StreamExecutionEnvironment, topic: String, bootstrapServers: String, groupId: String): DataStream[SensorReading] = {
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", bootstrapServers)
    properties.setProperty("group.id", groupId)

    val consumer = new FlinkKafkaConsumer[String](topic, new SimpleStringSchema(), properties).setStartFromLatest()

    env.addSource(consumer)
      .map(i => {
        val elems = i.split(",")
        SensorReading(elems(0), (elems(1).toDouble), (elems(2).toDouble * 1000).toLong)
      })
  }

}
