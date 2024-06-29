package cn.izualzhy

import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory

object UseTimerAsWindowApp extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment

  val sourceStream = SourceUtils.generateLocalSensorReadingStream(env)
    .assignAscendingTimestamps(r => r.eventTime)

  useTimerAsWindow(sourceStream)

  env.execute("UseTimerAsWindowApp")

  private def useTimerAsWindow(sourceStream: DataStream[SensorReading]): Unit = {
    sourceStream.keyBy(_.id)
      .process(new OneMinuteWindowProcessFunction)
  }

  private class OneMinuteWindowProcessFunction extends KeyedProcessFunction[String, SensorReading, String] {
    val logger = LoggerFactory.getLogger(classOf[OneMinuteWindowProcessFunction])

    private lazy val sumState = getRuntimeContext.getState(new ValueStateDescriptor[Double]("sum", classOf[Double]))
    override def processElement(value: SensorReading, ctx: KeyedProcessFunction[String, SensorReading, String]#Context, out: Collector[String]): Unit = {
      logger.info(s"processElement: ${value} i: ${value.id} timestamp:${ctx.timestamp()} currentProcessingTime:${ctx.timerService().currentProcessingTime()} currentWatermark:${ctx.timerService().currentWatermark()} getCurrentKey:${ctx.getCurrentKey}")

      if (sumState.value() == 0) {
        if (value.id.equals("sensor_a")) {
          val windowEndTimer: Long = (ctx.timestamp() / 60000L + 1) * 60000L - 1
          logger.info(s"register windowEndTimer: $windowEndTimer")
          ctx.timerService().registerEventTimeTimer(windowEndTimer)
        } else {
          val windowEndTimer: Long = (ctx.timestamp() / 120000L + 1) * 120000L - 1
          logger.info(s"register windowEndTimer: $windowEndTimer")
          ctx.timerService().registerEventTimeTimer(windowEndTimer)
        }

        sumState.update(value.temperature)
      } else {
        sumState.update(sumState.value() + value.temperature)
      }
    }


    override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[String, SensorReading, String]#OnTimerContext, out: Collector[String]): Unit = {
      logger.info(s"collect getCurrentKey:${ctx.getCurrentKey} sumState.value:${sumState.value()} ctx.timestamp:${ctx.timestamp()} timestamp:${timestamp}")
      out.collect(s"${ctx.getCurrentKey} ${sumState.value()}")

      sumState.clear()
    }
  }
}
