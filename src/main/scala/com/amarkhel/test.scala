package com.amarkhel

import scala.util.Try
import scala.util.Success
import scala.util.Failure

object test extends App {
  import scalaz.Reader
  type Key = Double
  trait Transaction
  type Work[A] = Reader[Transaction, A]
  
  object Database {
    object tr extends Transaction
    val map = new collection.mutable.HashMap[Key, Any]
    def run[T](work:Work[T]) : Try[T] = {
      try {
        startTransaction()
        val result = work.run(tr)
        commit()
        Success(result)
      } catch {
        case whatever:Throwable => rollback(); Failure(whatever)
      }
    }
    
    def startTransaction() = println("Start")
      def commit() = println("Commit")
      def rollback() = println("Rollback")
      
    def get[A](key:Key) : Work[Option[A]] = {
      Reader(Transaction => {map.get(key).map(_.asInstanceOf[A])})
    }
    
    val work = for {
      res <- Database.get[String](1.0)
    } yield res
    
    val result = Database.run(work)
    println(result.getOrElse(None).getOrElse("Not found"))
    
    List(1,2,3).foldLeft(0)(_ + _)
  }
}