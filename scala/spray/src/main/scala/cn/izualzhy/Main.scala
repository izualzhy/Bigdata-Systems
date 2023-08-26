package cn.izualzhy

import spray.json.DefaultJsonProtocol._
import spray.json._

import java.util.Date

case class TaskCheckpointQueryParam(id: Long, triggerTime: Date, apiUsername: Option[String])

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit object DateJsonFormat extends JsonFormat[Date] {
    def write(date: Date): JsValue = JsString(date.toString)
    def read(json: JsValue): Date = json match {
      case JsString(dateString) => new Date(dateString)
      case _ => throw new DeserializationException("Invalid date format")
    }
  }
  implicit val taskCheckpointQueryParamFormat: RootJsonFormat[TaskCheckpointQueryParam] = jsonFormat3(TaskCheckpointQueryParam)
}

object Main {
  def main(args: Array[String]): Unit = {
    import MyJsonProtocol._

    val s =
      """
        |{"id": 1113, "triggerTime": "2023-08-08T12:00:00.000Z"}""".stripMargin

    val taskCheckpointQueryParam = s.parseJson.convertTo[TaskCheckpointQueryParam]
    println(taskCheckpointQueryParam)
  }
}