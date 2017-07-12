package com.supergloo

import webscrapper.database.DB
import webscrapper.Location
import webscrapper.TournamentResult
import webscrapper.Result

object OmonLuckersApp {
  
  def isWin(t:(Boolean, TournamentResult, Result)) = {
    t._1 match {
      case true => t._3 == Result.MAFIA_WIN
      case false => t._3 == Result.GOROD_WIN
    }
  }
  
  def pointsEarned(t:(Boolean, TournamentResult, Result)) = {
    val win = isWin(t)
    if(win) {
      t._1 match {
    
      case true => t._2 match {
        case TournamentResult.OMON_1 => 0.5
        case TournamentResult.OMON_2 => 0.75
        case TournamentResult.OMON_3 => 0.875
        case TournamentResult.OMON_4 => 0.938
      }
      case false => t._2 match {
        case TournamentResult.OMON_1 => 0.5
        case TournamentResult.OMON_2 => 0.25
        case TournamentResult.OMON_3 => 0.125
        case TournamentResult.OMON_4 => 0.062
      }
      }
    } else 0.0
  }
  
  case class Stat(name:String, count:Int=0, countOmon1:Int=0, countOmon2:Int =0, countOmon3:Int = 0, countOmon4:Int = 0, countOmon1Win:Int=0, countOmon2Win:Int =0, countOmon3Win:Int = 0, countOmon4Win:Int = 0, points:Double=0.0, kef:Double =0.0)
  def main(args: Array[String]) = {
    //val playersMap = new collection.mutable.HashMap[String, Stat]()
    val games = DB().loadGames(year=2016, loc=Location.OZHA, minCount = 7)
    val filtered = games.filter(_.wasOmon).flatMap(g => {
      val round = g.statistics.omonRound
      val pl = round.get.playersOnOmon
      val res = pl.map(p => {
        (p.name, p.isMafia, g.tournamentResult, g.result)
      })
      res
    })
    
    val data = filtered.groupBy(_._1).map{case (k,v) => k -> v.map(a => (a._2, a._3, a._4))}
    val calculated = data.map(d => {
      val roles = d._2
      val count = roles.size
      val omon1 = roles.filter(_._2 == TournamentResult.OMON_1)
      val omon2 = roles.filter(_._2 == TournamentResult.OMON_2)
      val omon3 = roles.filter(_._2 == TournamentResult.OMON_3)
      val omon4 = roles.filter(_._2 == TournamentResult.OMON_4)
      val omon1Win = omon1.filter(isWin(_))
      val omon2Win = omon2.filter(isWin(_))
      val omon3Win = omon3.filter(isWin(_))
      val omon4Win = omon4.filter(isWin(_))
      val points = roles.map(pointsEarned).sum
      val kef = points/count
      new Stat(d._1, count, omon1.size, omon2.size, omon3.size, omon4.size, omon1Win.size, omon2Win.size, omon3Win.size, omon4Win.size, points, kef)
    })
    val sorted = calculated.filter(_.count > 9).toSeq.sortBy(_.kef).reverse.zipWithIndex
    println(s"[spoiler=Статистика лаки омонщиков 2016]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Игрок[/th][th]Коэффициент удачливости[/th][th]Количество омонов[/th][th]омон 1[/th][th]омон 1 побед[/th][th]омон 2[/th][th]омон 2 побед[/th][th]омон 3[/th][th]омон 3 побед[/th][th]омон 4[/th][th]омон 4 побед[/th][/tr]")
    sorted.foreach(r => {
      println(s"[tr][td]${r._2 + 1}[/td][td][nick]${r._1.name}[/nick][/td][td]${r._1.kef}[/td][td]${r._1.count}[/td][td]${r._1.countOmon1}[/td][td]${r._1.countOmon1Win}[/td][td]${r._1.countOmon2}[/td][td]${r._1.countOmon2Win}[/td][td]${r._1.countOmon3}[/td][td]${r._1.countOmon3Win}[/td][td]${r._1.countOmon4}[/td][td]${r._1.countOmon4Win}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
}
}