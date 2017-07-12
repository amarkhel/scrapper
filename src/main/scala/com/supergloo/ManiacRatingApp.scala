package com.supergloo

import webscrapper.database.DB
import webscrapper.Location
import webscrapper.util.StatUtils
import webscrapper.TournamentResult
import scala.collection.mutable.HashMap
import webscrapper.Role
import scala.collection.immutable.ListMap
import scala.collection.mutable.ListBuffer
import com.supergloo.Util._


object ManiacRatingApp {

  def groupManiac(op : (Int,Int)) = {
    val ind = op._1
    mapToGroup(ind)
  }
  
  def mapToGroup(countPlayers:Int) = {
    countPlayers match {
      case i:Int if (i < 16) => "12-15"
      case i:Int if (i>= 16 && i < 19) => "16-18"
      case i:Int if (i >= 19) => "19-21"
    }
  }
  
  def kefs(games:List[webscrapper.Game]) = {
    val mapped = games.map(g => g.countPlayers -> g.statistics.countManiacKills)
    val grouped = groupBy(mapped, groupManiac)
    grouped.map(g => g._1 -> 1/average(g._2))
  }
  
  case class Stat(name:String, count:Int=0, countKills:Int=0, kef:Double=0, points:Double=0, possiblePoints:Double=0, countOmon1:Int=0, countOmon2:Int=0, countOmon3:Int=0, averageKills:Double =0.0)
  
  def main(args: Array[String]) = {
    implicit val year = 2016
    implicit val location = Location.OZHA
    implicit val header = "Рейтинг маньяков"
    val games = DB().loadGames(year, location, "", 12)
    val playersMap = new collection.mutable.HashMap[String, Stat]()
    val koeff = kefs(games)
    val results = games.map(g => {
      val name = g.players.find(_.isManiac).map(_.name).get
      (name, g.countPlayers, g.tournamentResult, g.statistics.countManiacKills)
    })
    results.groupBy(_._1).foreach(result => {
      val name = result._1
      val gamess = result._2
      gamess.foreach(g => {
        val stat = playersMap.getOrElse(name, new Stat(name))
        val count = stat.count + 1
        val countKill = stat.countKills + g._4
        val pointsOneKill = koeff.get(mapToGroup(g._2)).get
        val possiblePoints = stat.possiblePoints + 1
        val points = stat.points +  pointsOneKill * g._4
        val k = points/possiblePoints
        val omon1 = stat.countOmon1 + (if(g._3 == TournamentResult.OMON_1) 1 else 0)
        val omon2 = stat.countOmon2 + (if(g._3 == TournamentResult.OMON_2) 1 else 0)
        val omon3 = stat.countOmon3 + (if(g._3 == TournamentResult.OMON_3) 1 else 0)
        val average = countKill.toDouble / count
        playersMap.update(name, new Stat(name, count, countKill, k, points, possiblePoints, omon1, omon2, omon3, average))
      })
      
    })
    val rating = playersMap.map(_._2).toSeq.filter(_.count > 5).sortBy(_.kef).reverse.zipWithIndex
    val headers = new ListBuffer[(String, Boolean, (Stat, Int) => Any)]
    headers += ("Место", false, (stat, rate) => rate)
    headers += ("Ник", true, (stat, rate) => stat.name)
    headers += ("Число игр", false, (stat, rate) => stat.count)
    headers += ("КПД", false, (stat, rate) => stat.kef)
    headers += ("Всего трупаков", false, (stat, rate) => stat.countKills)
    headers += ("За игру", false, (stat, rate) => stat.averageKills)
    headers += ("Одинарный омон", false, (stat, rate) => stat.countOmon1)
    headers += ("Двойной омон", false, (stat, rate) => stat.countOmon2)
    headers += ("Тройной омон", false, (stat, rate) => stat.countOmon3)
    printTable[Stat](rating.toList, headers.toList)
  }
}