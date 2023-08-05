package cn.izualzhy

import scala.concurrent.duration._
import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp
import spray.http._

import scala.util.{Failure, Success}

object SprayHelloWorld extends App with SimpleRoutingApp {
  implicit val system = ActorSystem("my-system")
  import system.dispatcher

  case class UFO(id: Int)

  println(getClass.getSimpleName)
  println(getClass.getSimpleName.replaceAll("\\$",""))

  startServer(interface = "localhost", port = 8081) {
    get {
      pathSingleSlash {
        redirect("/hello", StatusCodes.Found)
      } ~
        path("hello") {
          complete {
            <html>
              <h1>Say hello to <em>spray</em> on <em>spray-can</em>!</h1>
              <p>(<a href="/stop?method=post">stop server</a>)</p>
            </html>
          }
        }
    } ~
      (post | parameter('method ! "post")) {
        path("stop") {
          complete {
            system.scheduler.scheduleOnce(1.second)(system.shutdown())(system.dispatcher)
            "Shutting down in 1 second..."
          }
        }
      }
  }.onComplete{
    case Success(b) =>
      println(s"Successfully bound to ${b.localAddress}")
    case Failure(exception) =>
      println(exception.getMessage)
      system.shutdown()
  }
}
