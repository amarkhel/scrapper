package com.supergloo

import webscrapper.database.DB
import webscrapper.Location
import scala.collection.mutable.ListBuffer
import scala.collection.immutable.ListMap

object PairsApp {
  
  def even[A](l: Array[A]) = l.zipWithIndex.collect { case (e, i) if (i % 2) == 0 => e }
  
  def parsePlayers(players: String) = {
    val arr = players.split(",").map(_.replaceAll("name =", "")).map(_.replaceAll("role =", "")).map(_.trim).map(_.replaceAll("Любит на 99,9", "Любит на 99"))
    val ev = even(arr)
    val p =ev.toList.sortBy(identity)
    p
  }
  
  def pairs(list:List[String]) = {
    def pairsInn(acc:ListBuffer[(String, String)], players:List[String]) : List[(String, String)] = {
      players match {
        case (head :: tail) => {
          val p = for (maf <- tail) yield (head, maf)
          acc ++=(p)
          pairsInn(acc, tail)
        }
        case _ => acc.toList
      }
    }
    pairsInn(new ListBuffer[(String, String)](), list)
  }
  
  def main(args: Array[String]): Unit = {
    val games = DB().loadforLocation(2016, Location.OZHA)
    val players = games.map(_._2)
    val all = players.flatMap(g => parsePlayers(g))
    
    val counts = all.groupBy(identity).map(f => f._1 -> f._2.size).filter(_._2 > 30)
    val pair = players.flatMap(g => pairs(parsePlayers(g))).filter(d => counts.get(d._1).isDefined && counts.get(d._1).get > 30 && counts.get(d._2).isDefined && counts.get(d._2).get > 30)
    //val distinct = pair.map(_._1).distinct.union(pair.map(_._2).distinct).distinct
    val p = pair.groupBy(identity).map(f => f._1 -> f._2.size).filter(_._2 > 10)
    val sorted = ListMap(p.toSeq.sortBy(_._2).reverse:_*).filter(_._2 > 10)
    val d = sorted.map(f => {
      val name1 = f._1._1
      val name2 = f._1._2
      val count = f._2
      val percent1 = count.toDouble / counts.get(name1).get.toDouble * 100
      val percent2 = count.toDouble / counts.get(name2).get.toDouble * 100
      val maxPercent = if (percent1 > percent2) percent1 else percent2
      val averagePercent = (percent1 + percent2)/2
      (name1, name2, count, percent1, percent2, maxPercent, averagePercent)
    })
    val result = d.toList.sortBy(_._3).reverse.zipWithIndex
    println(s"[spoiler=Статистика часто играющих вместе на крестах]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Ник[/th][th]Ник[/th][th]Количество игр вместе[/th][th]Процент игр первого игрока[/th][th]Процент игр второго игрока[/th][/tr]")
    result.foreach(res => {
      println(s"[tr][td]${res._2 + 1}[/td][td][nick]${res._1._1}[/nick][/td][td][nick]${res._1._2}[/nick][/td][td]${res._1._3}[/td][td]${res._1._4}[/td][td]${res._1._5}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
}