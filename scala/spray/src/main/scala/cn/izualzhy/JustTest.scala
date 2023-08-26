package cn.izualzhy
import spray.json._
import spray.json.DefaultJsonProtocol._

object JustTest extends App {

  var x: Option[Int] = Some(1)
  println(x.map(_ * 100))
  println(x.flatMap(i => Some(i* 100)))

  x = None
  println(x.map(_ * 100))
  println(x.flatMap(i => Some(i * 100)))
}
