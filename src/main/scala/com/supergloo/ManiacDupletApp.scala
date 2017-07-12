package com.supergloo

import webscrapper.database.DB
import webscrapper.Location
import webscrapper.util.StatUtils
import webscrapper.TournamentResult
import scala.collection.mutable.HashMap
import webscrapper.Role
import scala.collection.immutable.ListMap
import scala.collection.mutable.ListBuffer


object ManiacDupletApp {
  
  case class Stat(name:String, count:Int=0, countDuplet:Int=0, maxDuplets:Int =0, kef:Double =0.0, maxKills:Int = 0)
  def main(args: Array[String]) = {
    implicit val year = 2016
    implicit val location = Location.OZHA
    implicit val header = "Рейтинг маньяков делающих дубли"
    val games = DB().loadGames(year, location, "", 12)
    val playersMap = new collection.mutable.HashMap[String, Stat]()
    val calc = games.map(g => {
      val pl = g.players.find(_.isManiac).get
      val name = pl.name
      (name, g.statistics.maniacDuplets(pl), g.statistics.countManiacKills)
    })
    calc.groupBy(_._1).foreach(c => {
      val name = c._1
      val gamess = c._2
      gamess.foreach(g => {
        val stat = playersMap.getOrElse(name, new Stat(name))
        val count = stat.count + 1
        val countDuplets = stat.countDuplet + g._2
        val max = if(g._2 > stat.maxDuplets) g._2 else stat.maxDuplets
        val k = countDuplets.toDouble/count
        val maxKills = if(g._3 > stat.maxKills) g._3 else stat.maxKills
        playersMap.update(name, new Stat(name, count, countDuplets, max, k, maxKills))
      })
    })
    val rating = playersMap.map(_._2).toSeq.filter(_.count > 9).sortBy(_.kef).reverse.zipWithIndex
    val headers = new ListBuffer[(String, Boolean, (Stat, Int) => Any)]
    headers += ("Место", false, (stat, rate) => rate)
    headers += ("Ник", true, (stat, rate) => stat.name)
    headers += ("Число игр", false, (stat, rate) => stat.count)
    headers += ("Всего дублей", false, (stat, rate) => stat.countDuplet)
    headers += ("За игру", false, (stat, rate) => stat.kef)
    headers += ("Максимум дублей", false, (stat, rate) => stat.maxDuplets)
    headers += ("Максимум трупов", false, (stat, rate) => stat.maxKills)
    Util.printTable[Stat](rating.toList, headers.toList)
  }
}