package cn.izualzhy

import org.apache.flink.api.common.state.ValueStateDescriptor
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory

object ContinuousTempRiseAlertApp extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
//  env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
  env.setParallelism(1)

  private val bootStrapServers = args(2)
  private val sensorReadingTopic = args(3)
  private val groupId = args(4)
  private val sensorReading = SourceUtils.generateKafkaSensorReadingStream(env, sensorReadingTopic, bootStrapServers, groupId)

  sensorReading.keyBy(r => r.id)
    .process(new TemperatureRiseAlertFunction)
    .addSink(new SinkFunction[String] {
      val logger = LoggerFactory.getLogger(classOf[TemperatureRiseAlertFunction])

      override def invoke(value: String): Unit = {
        logger.info(s"receive value: ${value}")
      }
    })

  private class TemperatureRiseAlertFunction extends KeyedProcessFunction[String, SensorReading, String] {
    private lazy val lastTempState = getRuntimeContext.getState(new ValueStateDescriptor[Double]("last-temp", classOf[Double]))
    private lazy val riseRangeTimerState = getRuntimeContext.getState(new ValueStateDescriptor[Long]("rise-range-timer", classOf[Long]))

    override def processElement(i: SensorReading, context: KeyedProcessFunction[String, SensorReading, String]#Context, collector: Collector[String]): Unit = {
      val lastTemp = lastTempState.value()
      val curTemp = i.temperature
      lastTempState.update(curTemp)
      val riseRangeTimer = riseRangeTimerState.value()

      if (lastTemp == 0L || curTemp <= lastTemp) {
        if (riseRangeTimer != 0L) {
          context.timerService().deleteProcessingTimeTimer(riseRangeTimer)
          riseRangeTimerState.clear()
        }
      } else {
        if (riseRangeTimer == 0L) {
          val timerTs = context.timerService().currentProcessingTime() + 60 * 1000L
          context.timerService().registerProcessingTimeTimer(timerTs)
          riseRangeTimerState.update(timerTs)
        }
      }

      collector.collect(s"getCurrentKey:${context.getCurrentKey} curTemp:${curTemp} lastTemp:${lastTemp}")
    }

    override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[String, SensorReading, String]#OnTimerContext, out: Collector[String]): Unit = {
      out.collect(s"${ctx.getCurrentKey} rise range timer fired")
      riseRangeTimerState.clear()
    }
  }

  env.execute(this.getClass.getName)
}
