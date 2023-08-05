package cn.izualzhy

import akka.actor._
import com.typesafe.config.ConfigFactory

class DeadLetterListener extends Actor {
  def receive: Receive = {
    case d: DeadLetter =>
      println(s"Received DeadLetter: $d")
    // 在这里处理DeadLetter消息
    case s: String =>
      println(s"Received String:${s}")
    case i =>
      println(s"Received :${i}")
  }
}

object DeadLetterSample extends App {
  val config = ConfigFactory.parseString(
    """
  akka {
    log-dead-letters = on
    log-dead-letters-during-shutdown = on
    loglevel = DEBUG
  }
  """
  )
  val system = ActorSystem("MySystem", config)
  val listener = system.actorOf(Props[DeadLetterListener], name = "deadLetterListener")
  listener ! "init"

  system.eventStream.subscribe(listener, classOf[AllDeadLetters])
  println(s"deadLetters: ${system.deadLetters}")

  // 在这里发送一些消息，模拟DeadLetter的情况
  val actor1 = system.actorOf(Props.empty)
  val actor2 = system.actorOf(Props.empty)
  val helloActor = system.actorOf(Props[HelloActor], "hello_actor")
  (1 to 100).foreach( i => {
    actor1 ! "Hello"
    actor2 ! PoisonPill
  })
  helloActor ! "Hello"

  // 等待一段时间，以确保DeadLetter有足够的时间被处理
  Thread.sleep(1000000)

//  system.awaitTermination()
  system.terminate()
}
