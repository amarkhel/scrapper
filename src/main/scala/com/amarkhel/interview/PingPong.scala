package com.amarkhel.interview

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props

object PingPong extends App {
  abstract sealed trait Message
  case object PongMessage extends Message
  case object StartMessage extends Message
  case object StopMessage extends Message
  case object PingMessage extends Message
  class Pong extends Actor {
    var count = 0 
    def increment = count += 1
    def receive = {
      
      case PingMessage => {
        println("ping received") ;
        increment
        if (count > 100) {
          sender ! StopMessage
          context.stop(self)
        } else sender ! PongMessage
      }
      case PongMessage => System.out.println("Unexpected")
    }
  }
  class Ping(pong:ActorRef) extends Actor {
    def receive = {
      case StartMessage => println("Game started") ;pong ! PingMessage
      case PongMessage => println("pong received") ;pong ! PingMessage
      case StopMessage => println("Game ended") ;context.stop(self); context.system.shutdown()
    }
  }
  
  val system = ActorSystem("pingpong")
  val pong = system.actorOf(Props[Pong])
  val ping = system.actorOf(Props(new Ping(pong)))
  ping ! StartMessage
}