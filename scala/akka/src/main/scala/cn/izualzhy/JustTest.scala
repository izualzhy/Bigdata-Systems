package cn.izualzhy

import java.util.{Date, Properties}
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

object JustTest extends App {
  val m1: Map[String, String] = Map("a" -> "a1", "b" -> "b1")
  val m2: Map[String, String] = Map("c" -> "c2", "b" -> "b2")
  val m3: Map[Any, Any] = Map("d" -> "d3", "a" -> "a4")

  val properties = new Properties();
  properties.setProperty("a", "a_default")
  properties.setProperty("b", "b_default")
  properties.setProperty("c", "c_default")
  properties.setProperty("d", "d_default")
  properties.setProperty("e", "e_default")

  List(1)
    .map(_ => properties)
    .map(_.toMap)
    .map(m1 ++ m2 ++ m3 ++ _)
    .foreach(println)

  val l = List(
    ('a', 'b', 1),
    ('a', '*', 2),
    ('c', '*', 3)
  )

  println(new Date(1691052340653L))

}
