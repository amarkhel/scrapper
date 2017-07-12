package com.amarkhel

import scala.util.Try
import scala.util.Success
import scala.util.Failure



object TestApp extends App {
  ReaderSample
}

object ReaderSample {

  import scalaz.Reader

  type Key = Int

  trait Transaction
  
  type Work[A] = Reader[Transaction, A]
  
  object Database {

    val map = new collection.mutable.HashMap[Key, Any]
    object MyTransaction extends Transaction

    // Run now requires Work
    def run[T](work: Work[T]): Try[T] =
      try {
        startTransaction()
        val result = work.run(MyTransaction)
        commit()
        Success(result)
      } catch {
        case whatever:Throwable => rollback(); Failure(whatever)
      }

    def startTransaction() = {println("Start transaction")}
    def commit() = {println("Commit")}
    def rollback() = {println("Rollback")}

    // lift operations into Work - note both of these do nothing here
    def put[A](key: Key, a: A): Work[Unit] =
      Reader(Transaction => {map.put(key, a)})
      
    def delete[A](key: Key): Work[Unit] =
      Reader(Transaction => {map.remove(key)})
      
    def deleteUnsafe[A](key: Key): Work[Unit] =
      Reader(Transaction => {throw new Exception("")})
      
    def update[A](key: Key, a: A): Work[Unit] =
      Reader(Transaction => {map.update(key, a)})

    def find[A](key: Key): Work[Option[A]] =
      Reader(Transaction => map.get(key).map(_.asInstanceOf[A]))
  }

  // the program
  val work =
    for {
      _ <- Database.put(1, 123)
      _ <- Database.delete(1)
      _ <- Database.put(1, 123)
      _ <- Database.update(1, 124)
      _ <- Database.deleteUnsafe(-1)
      found <- Database.find[Int](1)
    } yield found
    
  val result = Database.run(work)
  println("result is " + result.getOrElse(None).getOrElse("Not found"))
  
  val work2 =
    for {
      _ <- Database.put(2, "Bar")
      found <- Database.find[String](3)
    } yield found

  // now run the program
  val result2 = Database.run(work2)
  println("result is " + result2.getOrElse(None).getOrElse("Not found"))
  
  val work3 =
    for {
      _ <- Database.put(2, "Bar")
      found <- Database.find[String](2)
    } yield found

  // now run the program
  val result3 = Database.run(work3)
  println("result is " + result3.getOrElse(None).getOrElse("Not found"))
  
  val work4 = 
    for {
      _ <- Database.put(1, 1.9)
      result <- Database.find[Double](1)
    } yield result
    
    val result4 = Database.run(work4)
  println("result is " + result4.getOrElse(None).getOrElse("Not found"))
  
  Database.update(1, "2")
}