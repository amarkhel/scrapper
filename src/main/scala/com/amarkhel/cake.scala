package com.amarkhel

object cake extends App {
  Myapp.run()
}

case class User(id: Int, name: String, age: Int)

trait Persistence {
  def startPersistance() : Unit
}

trait Middler {
  def startMiddler() : Unit
}

trait FrontEnd {
  def startUI() : Unit
}

trait Application {
  self : Persistence with Middler with FrontEnd =>
    
  def run() = {
    startPersistance()
    startMiddler()
    startUI()
  }
}

trait Database extends Persistence {
  override def startPersistance() = {
    println("Start persistance");
  }
}

trait Service extends Middler {
  override def startMiddler() = {
    println("Start middler");
  }
}

trait UI extends FrontEnd {
  override def startUI() = {
    println("Start UI");
  }
}

object Myapp extends Application with Database with Service with UI