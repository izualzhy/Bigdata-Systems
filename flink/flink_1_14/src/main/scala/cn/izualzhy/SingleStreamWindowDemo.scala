package cn.izualzhy

import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

object SingleStreamWindowDemo extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(1)

  env.fromElements(
      SensorReading("sensor_a", 2.1, 1000L),
      SensorReading("sensor_a", 1.1, 2000L),
      SensorReading("sensor_b", 0.1, 2500L),
      SensorReading("sensor_a", 3.6, 3000L),
      SensorReading("sensor_a", 4.5, 4000L),
      SensorReading("sensor_a", 5.0, 5000L),
    ).assignAscendingTimestamps(_.eventTime)
    .keyBy(_.id)
    .window(TumblingEventTimeWindows.of(Time.seconds(5)))
    .process(new ProcessWindowFunction[SensorReading, (String, Double, Long), String, TimeWindow] {
      override def process(key: String, context: Context, elements: Iterable[SensorReading], out: Collector[(String, Double, Long)]): Unit = {
        println(s"now we process with key: $key and window: ${context.window}")
        println(s"elements: \n\t${elements.toList.mkString("\n\t")}")
        out.collect(key, elements.minBy(_.temperature).temperature, context.window.getEnd)
      }
    })
    //    .reduce((a, b) => SensorReading(a.id, a.temperature.min(b.temperature), 0L))
    .print("min temperature ")

  env.execute()
}
