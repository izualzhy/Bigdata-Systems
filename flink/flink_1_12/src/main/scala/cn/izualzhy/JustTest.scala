package cn.izualzhy

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

import scala.collection.JavaConverters._

object JustTest extends App {
  (1 to 10).foreach( i => {
    println(s"rand 2, value:${scala.util.Random.nextInt(2)}")
  })

  (1 to 10).foreach(i => {
    println(s"rand 1, value:${scala.util.Random.nextInt(1)}")
  })

  (1 to 10).foreach(i => {
    println(s"rand 0, value:${scala.util.Random.nextInt(0)}")
  })

}
