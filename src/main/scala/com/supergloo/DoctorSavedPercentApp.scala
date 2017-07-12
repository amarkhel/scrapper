package com.supergloo

import webscrapper.Location
import webscrapper.util.StatUtils
import webscrapper.TournamentResult
import scala.collection.mutable.HashMap
import webscrapper.Role
import scala.collection.immutable.ListMap
import webscrapper.database.DB

object DoctorSavedPercentApp {
  
  def main(args:Array[String]) = {
    //val playersMap = new collection.mutable.HashMap[String, Stat]()
    val games = DB().loadGames(year=2016, loc=Location.OZHA, minCount = 12)
    val filtered = games.filter(_.statistics.doctorAttemptedFirstRoundPlayer.isDefined).map(g => {
        val pl = g.statistics.doctorAttemptedFirstRoundPlayer.get
        val name = pl.name
        val success = g.statistics.firstMafiaRound.get.doctorSavedPlayerRole.isDefined
        (name, success)
    })
    val data = filtered.groupBy(_._1).map{case (k,v) => k -> v.map(_._2)}
    case class Stat(name:String, countTried:Int, countSaved:Int, kef:Double)
    val calculated = data.map(d => {
      val roles = d._2
      
      val filt = roles.filter(_ == true)
      val kef = filt.size.toDouble / roles.size
      val saved = filt.size
      new Stat(d._1, roles.size, saved, kef)
    })
    val sorted = calculated.filter(_.countTried > 4).toSeq.sortBy(_.kef).reverse.zipWithIndex
    println(s"[spoiler=Статистика 2016 кого больше всего вылечили первым ходом]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Игрок[/th][th]Коэффициент лечения [/th][th]Количество лечений[/th][th]Количество успешных лечений[/th][/tr]")
    sorted.foreach(r => {
      println(s"[tr][td]${r._2 + 1}[/td][td][nick]${r._1.name}[/nick][/td][td]${r._1.kef}[/td][td]${r._1.countTried}[/td][td]${r._1.countSaved}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
}