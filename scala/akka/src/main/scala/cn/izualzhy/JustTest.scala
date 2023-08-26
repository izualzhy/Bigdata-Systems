package cn.izualzhy

import java.sql.Timestamp
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

  val date = new Date()
  println(date.getTime)
  println(new Timestamp(date.getTime))
  val year = 2023
  val month = 7 // August (0-based index)
  val day = 8
  val dateFromComponents1 = new Date(year - 1900, month, day)
  val dateFromComponents2 = new Date(year, month, day)
  println(dateFromComponents1)
  println(dateFromComponents2)

  object CheckpointStatus extends Enumeration {
    val completed = Value(0, "COMPLETED")
    val failed = Value(1, "FAILED")

    def toID(status: String): Int = status match {
      case "COMPLETED" => completed.id
      case "FAILED" => failed.id
      case _ => -1
    }
  }

  println(CheckpointStatus.completed)
  println(CheckpointStatus.completed.id)
}
