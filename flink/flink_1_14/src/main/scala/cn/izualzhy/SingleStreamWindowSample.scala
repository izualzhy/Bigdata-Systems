package cn.izualzhy

import org.apache.flink.api.common.functions.{ReduceFunction, RichMapFunction, RichReduceFunction}
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.{TumblingEventTimeWindows, TumblingProcessingTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.{GlobalWindow, TimeWindow}
import org.apache.flink.table.runtime.operators.window.CountWindow
import org.apache.flink.util.Collector
import org.slf4j.{Logger, LoggerFactory}


object SingleStreamWindowSample extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
  env.setParallelism(1)

  val source = if (args.length > 0) {
    val bootStrapServers = args(2)
    val sensorReadingTopic = args(3)
    val groupId = args(4)

    SourceUtils.generateKafkaSensorReadingStream(env, sensorReadingTopic, bootStrapServers, groupId)
  } else {
    SourceUtils.generateLocalSensorReadingStream(env)
  }
  private val sensorReading = source
    .assignAscendingTimestamps(r => r.eventTime)

  //  minMaxByReduce(sensorReading)
  //  minMaxByProcessWindow(sensorReading)
  testCustomTrigger(sensorReading)

  env.execute(this.getClass.getName)

  def minMaxByReduce(sensorReading: DataStream[SensorReading]) = {
    sensorReading
      .map(new RichMapFunction[SensorReading, (String, Double, Double)] {
        val logger: Logger = LoggerFactory.getLogger(this.getClass)

        override def map(r: SensorReading) = {
          logger.info(s"map function this:${this} r:${r}")
          (r.id, r.temperature, r.temperature)
        }
      }).keyBy(_._1)
      .window(TumblingEventTimeWindows.of(Time.seconds(5)))
      .reduce(new ReduceFunction[(String, Double, Double)]() {
        val logger: Logger = LoggerFactory.getLogger(this.getClass)

        override def reduce(r1: (String, Double, Double), r2: (String, Double, Double)): (String, Double, Double) = {
          logger.info(s"reduce this:${this} r1:${r1} r2:${r2}")
          (r1._1, r1._2.min(r2._2), r1._3.max(r2._3))
        }
      }).addSink(new RichSinkFunction[(String, Double, Double)] {
        val logger: Logger = LoggerFactory.getLogger(this.getClass)

        override def invoke(value: (String, Double, Double), context: SinkFunction.Context): Unit = {
          logger.info(s"sink this:${this} value:${value}")
        }
      })
  }

  case class MinMaxTemp(id: String, min: Double, max: Double, endTs: Long)
  def minMaxByProcessWindow(sensorReading: DataStream[SensorReading]) = {
    class HighAndLowTempProcessFunction extends ProcessWindowFunction[SensorReading, MinMaxTemp, String, TimeWindow] {
      val logger: Logger = LoggerFactory.getLogger(this.getClass)

      override def process(key: String, context: Context, elements: Iterable[SensorReading], out: Collector[MinMaxTemp]): Unit = {
        logger.info(s"key:${key} context:${context} elements:${elements}")
        val temps = elements.map(r => r.temperature)
        val windowEnd = context.window.getEnd
        logger.info(s"windowEnd:${windowEnd} key:${key} ${temps.min} ${temps.max}")
        out.collect(MinMaxTemp(key, temps.min, temps.max, windowEnd))
      }
    }

    sensorReading.keyBy(_.id)
      .window(TumblingEventTimeWindows.of(Time.seconds(5)))
      .process(new HighAndLowTempProcessFunction)
  }

  private def testCustomTrigger(sensorReading: DataStream[SensorReading]) = {
    class CustomProcessFunction extends ProcessWindowFunction[SensorReading, String, String, TimeWindow] {
      val logger: Logger = LoggerFactory.getLogger(this.getClass)

      override def process(key: String, context: Context, elements: Iterable[SensorReading], out: Collector[String]): Unit = {
        logger.info(s"key:${key}\n\telements:${elements.mkString(",", "[", "]")}")

//        val stack = Thread.currentThread().getStackTrace.map(_.toString)
//          .mkString("\n\t")
//        logger.info(s"stack:\n${stack}")

        val windowEnd = context.window.getEnd
        out.collect(s"key:${key} windowEnd:${windowEnd}")
      }
    }

    // test event time
//    sensorReading.keyBy(_.id)
//      .window(TumblingEventTimeWindows.of(Time.seconds(5)))
//      //      .trigger(MockEventTimeTrigger.create())
//      .trigger(new EventTimeAndCountTrigger(3))
//      .process(new CustomProcessFunction)

    // test process time
//    sensorReading.keyBy(_.id)
//      .window(TumblingProcessingTimeWindows.of(Time.seconds(60)))
//      .trigger(MockProcessingTimeTrigger.create())
//      .process(new CustomProcessFunction)

    // test count
    sensorReading.setParallelism(2).
      keyBy(_.id)
      .countWindow(3)
      .process(new ProcessWindowFunction[SensorReading, String, String, GlobalWindow] {
        val logger: Logger = LoggerFactory.getLogger(this.getClass)

        override def process(key: String, context: Context, elements: Iterable[SensorReading], out: Collector[String]): Unit = {
          logger.info(s"key:${key} context:${context} count:${elements.size} elements:${elements}")
          logger.info(s"context.window:${context.window} context.windowState:${context.windowState} context.globalState:${context.globalState}")
          out.collect(s"key:${key} count:${elements.size} elements:${elements}")
        }
      }).setParallelism(2)
  }

}
