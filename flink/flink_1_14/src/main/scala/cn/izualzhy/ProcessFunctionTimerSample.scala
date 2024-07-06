package cn.izualzhy

import org.apache.flink.streaming.api.functions.{KeyedProcessFunction, ProcessFunction}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

object ProcessFunctionTimerSample extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(1)

  testEventTime
//  testProcessTime()

  private def testProcessTime() = {
    val source = SourceUtils.generateLocalSensorReadingStream(env)

    source.keyBy(r => r.id)
      .process(new KeyedProcessFunction[String, SensorReading, String] {
        override def processElement(value: SensorReading, ctx: KeyedProcessFunction[String, SensorReading, String]#Context, out: Collector[String]): Unit = {
          val stack = Thread.currentThread().getStackTrace.map(_.toString)
            .mkString("\n\t")
          println(s"stack:\n${stack}\n\n")
          ctx.timerService().registerProcessingTimeTimer(System.currentTimeMillis() + 10000)

          out.collect(s"${value.id} ${ctx.timerService().currentWatermark()}")
        }

        override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[String, SensorReading, String]#OnTimerContext, out: Collector[String]): Unit = {
          val stack = Thread.currentThread().getStackTrace.map(_.toString)
            .mkString("\n\t")
          println(s"stack:\n${stack}\n\n")

          println(s"onTimer: $timestamp")
        }
      })
  }

  private def testEventTime = {
    val source = env.fromElements(
      SensorReading("sensor_a", 2.1, 1000L),
      SensorReading("sensor_a", 3.6, 3000L),
      SensorReading("sensor_b", 4.5, 4000L),
      SensorReading("sensor_a", 5.0, 5000L),
      SensorReading("sensor_a", 6.0, 6000L),
    ).assignAscendingTimestamps(_.eventTime)

//    testKeyedProcessFunctionOnKeyedStream
    //  testProcessFunctionOnKeyedStream
    testProcessFunction

    def testKeyedProcessFunctionOnKeyedStream = {
      source.keyBy(_.id)
        .process(new KeyedProcessFunction[String, SensorReading, String] {
          override def processElement(value: SensorReading, ctx: KeyedProcessFunction[String, SensorReading, String]#Context, out: Collector[String]): Unit = {
            val stack = Thread.currentThread().getStackTrace.map(_.toString)
              .mkString("\n\t")
            println(s"stack:\n${stack}\n\n")

            ctx.timerService().registerEventTimeTimer(((value.eventTime / 5000L) + 1) * 5000)
            out.collect(s"${value.id} ${ctx.timerService().currentWatermark()}")
          }

          override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[String, SensorReading, String]#OnTimerContext, out: Collector[String]): Unit = {
            val stack = Thread.currentThread().getStackTrace.map(_.toString)
              .mkString("\n\t")
            println(s"stack:\n${stack}\n\n")

            println(s"onTimer: $timestamp")
          }
        })
        .print()
    }

    def testProcessFunctionOnKeyedStream = {
      source.keyBy(_.id)
        .process(new ProcessFunction[SensorReading, String] {
          override def processElement(value: SensorReading, ctx: ProcessFunction[SensorReading, String]#Context, out: Collector[String]): Unit = {
            ctx.timerService().registerEventTimeTimer(((value.eventTime / 5000L) + 1) * 5000)
            out.collect(s"${value.id} ${ctx.timerService().currentWatermark()}")
          }

          override def onTimer(timestamp: Long, ctx: ProcessFunction[SensorReading, String]#OnTimerContext, out: Collector[String]): Unit = {
            println(s"onTimer: $timestamp")
          }
        }
        ).print()
    }

    def testProcessFunction = {
      source.process(new ProcessFunction[SensorReading, String] {
        override def processElement(value: SensorReading, ctx: ProcessFunction[SensorReading, String]#Context, out: Collector[String]): Unit = {
          // 如果不加 keyBy 就会报错:
          //        Caused by: java.lang.UnsupportedOperationException: Setting timers is only supported on a keyed streams.
          //          at org.apache.flink.streaming.api.operators.ProcessOperator$ContextImpl.registerEventTimeTimer(ProcessOperator.java:123)
          ctx.timerService().registerEventTimeTimer(((value.eventTime / 5000L) + 1) * 5000)
          out.collect(s"${value.id} ${ctx.timerService().currentWatermark()}")
        }

        override def onTimer(timestamp: Long, ctx: ProcessFunction[SensorReading, String]#OnTimerContext, out: Collector[String]): Unit = {
          println(s"onTimer: $timestamp")
        }
      }).print()
    }
  }

  env.execute()
}
