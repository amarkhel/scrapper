package com.supergloo

import webscrapper.database.DB
import webscrapper.Location

object TimeoutApp {
  def main(args:Array[String]) = {
    //val playersMap = new collection.mutable.HashMap[String, Stat]()
    val games = DB().loadGames(year=2016, loc=Location.OZHA, minCount = 7)
    val filtered = games.flatMap(g => {
        val pl = g.statistics.timeouters
        val res = g.players.map(p => {
          if (pl.contains(p)) (p.name, 1) else (p.name, 0)
        })
        res
    })
    val data = filtered.groupBy(_._1).map{case (k,v) => k -> v.map(_._2)}
    case class Stat(name:String, count:Int, countTimes:Int, kef:Double)
    val calculated = data.map(d => {
      val roles = d._2
      
      val filt = roles.filter(_ == 1)
      val kef = filt.size.toDouble / roles.size
      val saved = filt.size
      new Stat(d._1, roles.size, saved, kef)
    })
    val sorted = calculated.filter(_.count > 10).toSeq.sortBy(_.kef).reverse.zipWithIndex
    println(s"[spoiler=Статистика 2016 таймы]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Игрок[/th][th]Коэффициент таймов[/th][th]Количество таймов[/th][th]Количество игр[/th][/tr]")
    sorted.foreach(r => {
      println(s"[tr][td]${r._2 + 1}[/td][td][nick]${r._1.name}[/nick][/td][td]${r._1.kef}[/td][td]${r._1.countTimes}[/td][td]${r._1.count}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
}