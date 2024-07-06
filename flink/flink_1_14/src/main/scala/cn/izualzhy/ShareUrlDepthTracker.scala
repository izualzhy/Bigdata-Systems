package cn.izualzhy

import org.apache.flink.api.common.state.{MapState, MapStateDescriptor}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

import scala.collection.JavaConversions._

object ShareUrlDepthTracker extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(1)

  val source = SourceUtils.generateShareUrlData(env)
    .keyBy(_._3)

  calcDepth()

  env.execute()

  def calcDepth() = {
    source.process(new KeyedProcessFunction[String, (Int, Int, String), (String, Int)] {
      private lazy val depthState: MapState[Int, Int] = getRuntimeContext.getMapState(new MapStateDescriptor[Int, Int]("depth", classOf[Int], classOf[Int]))

      override def open(parameters: Configuration): Unit = {
        super.open(parameters)
      }

      override def processElement(value: (Int, Int, String), ctx: KeyedProcessFunction[String, (Int, Int, String), (String, Int)]#Context, out: Collector[(String, Int)]): Unit = {
        val from = value._1
        val to = value._2
        val url = value._3

        val fromDepth = if (depthState.contains(from)) depthState.get(from) else 0
        val toDepth = fromDepth + 1
        depthState.put(to, toDepth)

        // Calculate the max depth
        var maxDepth = 0
        for (depth <- depthState.values) {
          if (depth > maxDepth) maxDepth = depth
        }

        println(s"From: $from, To: $to, Url: $url, Depth: $toDepth")
      }
    })

  }

}
