package com.amarkhel.interview

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

object Coding extends App {
  abstract sealed trait Message
  case object PrintString extends Message 
  case object PrintDouble extends Message
  case object PrintInt extends Message
  class HelloActor extends Actor {
    def receive = {
      case PrintString => println("print string")
      case PrintInt => println("print int")
      case PrintDouble => println("print double")
      case _ => println("not defined ")
    }
  }
  
  val system = ActorSystem("HelloSystem")
  // (3) changed this line of code
  val helloActor = system.actorOf(Props[HelloActor], name = "helloactor")
  helloActor ! PrintString
  helloActor ! PrintInt
  helloActor ! PrintDouble
  helloActor ! None
}