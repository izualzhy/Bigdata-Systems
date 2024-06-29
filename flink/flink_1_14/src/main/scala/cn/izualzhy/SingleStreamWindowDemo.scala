package cn.izualzhy

import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time

object SingleStreamWindowDemo extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(1)

  env.fromElements(
    SensorReading("sensor_a", 2.1, 1000L),
    SensorReading("sensor_a", 1.1, 2000L),
    SensorReading("sensor_a", 3.6, 3000L),
    SensorReading("sensor_a", 4.5, 4000L),
    SensorReading("sensor_a", 5.0, 5000L),
  ).assignAscendingTimestamps(_.eventTime)
    .keyBy(_.id)
    .window(TumblingEventTimeWindows.of(Time.seconds(5)))
    .reduce((a, b) => SensorReading(a.id, a.temperature.min(b.temperature), 0L))
    .print()

  env.execute()
}
