package com.supergloo

import webscrapper.Location

object Util {
  
  def even[A](l:Array[A]) = l.zipWithIndex.collect {case (e,i) if ((i+1) % 2) == 0 => e}
  def odd[A](l:Array[A]) = l.zipWithIndex.collect {case (e,i) if ((i+1) % 2) == 1 => e}
  
  def groupBy[T, Y](list:List[(T, Y)], op: ((T, Y)) => String) = {
    list.groupBy(op).map{case (k, v) => k -> v.map(_._2)}
  }
  
  def average[T](list:List[T])(implicit num: Numeric[T]) = {
    import num._
    list.sum.toDouble / list.size
  }
  
  def printTable[T](results: List[(T, Int)], head:List[(String, Boolean, (T, Int) => Any)])(implicit location:Location, header:String, year:Int) = {
    val name = if(location == Location.SUMRAK) "Общий" else location.name
    println(s"[spoiler=${header} ${name} ${year}]")

    println(s"[table]")
    println(s"[tbody]")
    val headers = head.map(_._1).map(h => s"[th]$h[/th]").mkString
    println(s"[tr]$headers[/tr]")
    
    results.foreach(res => {
      val fields = head.map( field => {
        val op = field._3
        val isNick = field._2
        val evaled = op(res._1, res._2)
        val print = if(isNick) s"[nick]$evaled[/nick]" else s"$evaled"
        s"[th]$print[/th]"
      }).mkString
      println(s"[tr]$fields[/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
}