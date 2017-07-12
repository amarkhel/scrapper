package com.supergloo

import webscrapper.database.DB
import webscrapper.Location
import scala.collection.mutable.ListBuffer
import webscrapper.TournamentResult
import webscrapper.Role
import webscrapper.Result.GOROD_WIN

object VeselApp3 {

  def getPoints(role: Role, result: TournamentResult) = {
    val isMafia = role == Role.MAFIA || role == Role.BOSS
    isMafia match {
      case true => result match {
        case TournamentResult.GOROD_WIN => 0.0
        case TournamentResult.OMON_4    => 0.75
        case TournamentResult.OMON_3    => 0.75
        case TournamentResult.OMON_2    => 0.5
        case TournamentResult.OMON_1    => 0.25
        case TournamentResult.MAFIA_WIN => 1.0
      }
      case false => result match {
        case TournamentResult.GOROD_WIN => 1.0
        case TournamentResult.OMON_4    => 0.25
        case TournamentResult.OMON_3    => 0.25
        case TournamentResult.OMON_2    => 0.5
        case TournamentResult.OMON_1    => 0.75
        case TournamentResult.MAFIA_WIN => 0.0
      }
    }
  }
  
  def calculatePersonal(name: String) = {
    val list = ListBuffer[Double]()
    DB().loadGames(2016, Location.SUMRAK, name, 6).filter(_.statistics.wasLongTroika).filter(_.statistics.wasAliveOnTroika(name)).foreach(g => {
      val pl = g.findPlayer(name)
      
      if (pl.isDefined && !pl.get.isManiac) {
        val role = pl.get.role
        
        val points = getPoints(role, g.tournamentResult)
          list += points
      }
    })
      println(list.sum.toDouble / list.size + s"  ${list.size}")
    
  }
  
  def calculateGeneral = {
    case class Stat(name:String, val points:Double = 0, count:Int = 0, kpd:Double =0.0, countOmon:Int =0, countWin:Int =0, countLose:Int = 0)
    case class DoubleStat(name:String, count: Int = 0, val points:Double = 0.0, val kpd:Double = 0.0)
    val citStat = new collection.mutable.HashMap[String, Stat]()
    val mafStat = new collection.mutable.HashMap[String, Stat]()
    val overallStat = new collection.mutable.HashMap[String, DoubleStat]()
    val games = DB().loadGames(2016, Location.SUMRAK, "", 6).filter(_.statistics.wasLongTroika)
    
    games.foreach(g => {
      val players = g.statistics.alivedOnTroika
      players.foreach(p => {
        if(p.isManiac || g.tournamentResult == TournamentResult.OMON_2 || g.tournamentResult == TournamentResult.OMON_3) () else {
           val isMafia = p.isMafia
        val points = getPoints(p.role, g.tournamentResult)
        if(isMafia) {
          val stat = mafStat.getOrElse(p.name, new Stat(p.name)) 
          
          val count = stat.count + 1
          val po = stat.points + points
          val kpd = po /count
          val countWin = stat.countWin + (if(g.tournamentResult == TournamentResult.MAFIA_WIN) 1 else 0)
          val countLose = stat.countLose + (if(g.tournamentResult == TournamentResult.GOROD_WIN) 1 else 0)
          val countOmon = stat.countOmon + (if(g.tournamentResult == TournamentResult.OMON_1) 1 else 0)
          if(countWin == 0 && countLose == 0 && countOmon == 0) println(s"${g.id} - ${g.tournamentResult} - ${p}")
          mafStat.update(p.name, new Stat(p.name, po, count, kpd, countOmon, countWin, countLose))
        } else {
          val stat = citStat.getOrElse(p.name, new Stat(p.name))
          val count = stat.count + 1
          val po = stat.points + points
          val kpd = po /count
          val countWin = stat.countWin + (if(g.tournamentResult == TournamentResult.GOROD_WIN) 1 else 0)
          val countLose = stat.countLose + (if(g.tournamentResult == TournamentResult.MAFIA_WIN) 1 else 0)
          val countOmon = stat.countOmon + (if(g.tournamentResult == TournamentResult.OMON_1) 1 else 0)
          if(countWin == 0 && countLose == 0 && countOmon == 0) println(s"${g.id} - ${g.tournamentResult} - ${p}")
          citStat.update(p.name, new Stat(p.name, po, count, kpd, countOmon, countWin, countLose))
        }
        val ov = overallStat.getOrElse(p.name, new DoubleStat(p.name))
        val po = ov.points + points
        val co = ov.count + 1
        val kpd = po/co
        overallStat.update(p.name, new DoubleStat(p.name, co, po, kpd))
        }
      })
    })
    val overallRating = overallStat.map(_._2).filter(_.count > 19).toSeq.sortBy(_.kpd).reverse.zipWithIndex
    val mafiaRating = mafStat.map(_._2).filter(_.count > 9).toSeq.sortBy(_.kpd).reverse.zipWithIndex
    val citRating = citStat.map(_._2).filter(_.count > 9).toSeq.sortBy(_.kpd).reverse.zipWithIndex
    println(s"[spoiler=Рейтинг троечников Общий]")

   println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Ник[/th][th]Число игр[/th][th]КПД[/th][/tr]")
    overallRating.foreach(res => {
      val rate = res._1
      println(s"[tr][td]${res._2 + 1}[/td][td][nick]${rate.name}[/nick][/td][td]${rate.count}[/td][td]${rate.kpd}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
    
    println(s"[spoiler=Рейтинг троечников роль мафия]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Ник[/th][th]Число игр[/th][th]КПД[/th][th]Побед[/th][th]Проигрышей[/th][th]Омонов[/th][/tr]")
    mafiaRating.foreach(res => {
      val rate = res._1
      println(s"[tr][td]${res._2 + 1}[/td][td][nick]${rate.name}[/nick][/td][td]${rate.count}[/td][td]${rate.kpd}[/td][td]${rate.countWin}[/td][td]${rate.countLose}[/td][td]${rate.countOmon}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
    
    println(s"[spoiler=Рейтинг троечников роль честный]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Ник[/th][th]Число игр[/th][th]КПД[/th][th]Побед[/th][th]Проигрышей[/th][th]Омонов[/th][/tr]")
    citRating.foreach(res => {
      val rate = res._1
      println(s"[tr][td]${res._2 + 1}[/td][td][nick]${rate.name}[/nick][/td][td]${rate.count}[/td][td]${rate.kpd}[/td][td]${rate.countWin}[/td][td]${rate.countLose}[/td][td]${rate.countOmon}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }

  def main(args: Array[String]) = {
    /*calculatePersonal("весельчакджо")
    calculatePersonal("Lion")*/
    calculateGeneral
    }
}