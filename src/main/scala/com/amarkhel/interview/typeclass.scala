package com.amarkhel.interview

object typeclass extends App {
  
  case class Person(name:String, age:Int)
  case class File(name:String, path:String)
  case class Product(name:String, price:Double)
  case class Animal(name:String, model:String)
  
  case class JSON(s:String) {
    def print = println(s)
  }
  
  trait JsonObject[T] {
    def print(t:T):JSON
  }
  
  object JsonObject {
    implicit object PersonNamed extends JsonObject[Person]{
      def print(p:Person) = JSON(s"Name = ${p.name}, age = ${p.age}")
    }
    
    implicit object FileNamed extends JsonObject[File]{
      def print(p:File) = JSON(s"Name = ${p.name}, path = ${p.path}")
    }
    
    implicit object ProductNamed extends JsonObject[Product]{
      def print(p:Product) = JSON(s"Name = ${p.name}, price = ${p.price}")
    }
    
    implicit object AnimalNamed extends JsonObject[Animal]{
      def print(p:Animal) = JSON(s"Name = ${p.name}, model = ${p.model}")
    }
  }
  
  object JsonFormatter {
    def toJson[T](t:T)(implicit ev:JsonObject[T]) :JSON = {
      ev.print(t)
    }
  }
  val p = Person("Andrey", 32)
  val f = File("name", "path")
  val pr = Product("book", 32.4)
  val a = Animal("tiger", "tiger")

  JsonFormatter.toJson(p)
  JsonFormatter.toJson(f)
  JsonFormatter.toJson(pr)
  JsonFormatter.toJson(a)

/*  list.foreach(a => println(JsonFormatter.toJson(a)))*/
}