package com.amarkhel.interview

import scala.concurrent.Future

object TestApp extends App {
  println("Hi")
  import MagnetPattern.Magnet._
  println(((1 to 20).par fold "") ((s1, s2) => s"$s1 - $s2"))
  val w = new MagnetPattern.Work2
  println(w.complete("1"))
}

object MagnetPattern {
  sealed trait Magnet {
    type Result
    def call() : Result
  }
  
  class Work2 {
    def complete(magnet:Magnet):magnet.Result = magnet.call()
  }
  
  object Magnet {
    implicit def fromStrToMagnet(s:String):Magnet = new Magnet{
      type Result = Int
      def call() :Result = s.toInt
    }
    implicit def fromFutureToMagnet(s:Future[String]):Magnet = new Magnet{
      type Result = Int
      def call() :Result = s.value.map(_.get.toInt).get
    }
    implicit def fromListToMagnet(s:List[String]):Magnet = new Magnet{
      type Result = Int
      def call() :Result = s(0).toInt
    }
  }
}