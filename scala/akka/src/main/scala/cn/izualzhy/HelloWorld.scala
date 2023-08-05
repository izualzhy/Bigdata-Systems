package cn.izualzhy

import akka.actor.{Actor, ActorSystem, Props}

class HelloActor extends
  Actor {
  override def receive: Receive = {
    case "hello" => println("hello back at you")
    case r => println(s"huh? msg:${r}")
  }
}

object HelloWorld extends App {
  val system = ActorSystem("HelloSystem")
  // default Actor constructor
  val helloActor = system.actorOf(Props[HelloActor], "hello_actor")
  helloActor ! "hello"
  helloActor ! "buenos dias"

  system.terminate()
}
