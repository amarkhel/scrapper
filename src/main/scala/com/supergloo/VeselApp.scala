package com.supergloo

import webscrapper.database.DB
import webscrapper.Location
import scala.collection.mutable.ListBuffer
import webscrapper.TournamentResult
import webscrapper.Role
import webscrapper.Result.GOROD_WIN

object VeselApp {

  def calculateGenericStat() = {
    val games = DB().loadGames(2016, Location.SUMRAK, "", 6)
    
    val pl = games.filter(_.statistics.firstPrisonedName.isDefined).flatMap(g => {
      val firstpris = g.statistics.firstPrisonedName
        g.players.map(p => {
        if (p.name == firstpris.get) (p.name, 1) else (p.name, 0)
      })
      
    })
    val grouped = pl.groupBy(_._1)
    case class Stat(name: String, count: Int = 0, countPris: Int = 0, kef: Double = 0.0, percent: String = "")
    val playersMap = new ListBuffer[Stat]()
    grouped.foreach(g => {
      val name = g._1
      val games = g._2.map(_._2)
      playersMap += new Stat(name, games.size, games.sum, games.sum.toDouble / games.size, games.sum / games.size * 100 + "%")
    })
    val rating = playersMap.filter(_.count > 50).sortBy(_.kef).reverse.zipWithIndex
    println(s"[spoiler=Рейтинг первых посадок]")

    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Ник[/th][th]Число игр[/th][th]Число посадок[/th][th]Процент посадок[/th][/tr]")
    rating.foreach(res => {
      val rate = res._1
      println(s"[tr][td]${res._2 + 1}[/td][td][nick]${rate.name}[/nick][/td][td]${rate.count}[/td][td]${rate.countPris}[/td][td]${rate.kef}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }

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
    val games = DB().loadGames(2016, Location.SUMRAK, name, 8)
    val first = new ListBuffer[(Role, TournamentResult)]()
    val next = new ListBuffer[(Role, TournamentResult)]()
    games.foreach(g => {
      val firstpris = g.statistics.firstPrisonedName
      //.get
      val pl = g.findPlayer(name)
      if (pl.isDefined && firstpris.isDefined) {
        val role = g.findPlayer(name.trim).get.role
        val sum = (role, g.tournamentResult)
        if (role == Role.MANIAC) () else {
          if (name == firstpris.get) first += sum else next += sum
        }
      } else {
        println(s"$name not found in ${g.id} but should be ")
        println(s"$firstpris")
      }

    })
    val a1 = first.map(f => getPoints(f._1, f._2)).sum
    val a2 = next.map(f => getPoints(f._1, f._2)).sum
    val s1 = ("Первая посадка", a1 / first.size, first.size, next.size)
    val s2 = ("Не Первая посадка", a2 / next.size, first.size, next.size)
    (s1, s2)
    /*val grouped = pl.groupBy(_._1)
    case class Stat(name:String, count:Int = 0, countPris:Int =0, kef:Double =0.0, percent:String = "")
    val playersMap = new ListBuffer[Stat]()
    grouped.foreach(g => {
      val name = g._1
      val games = g._2.map(_._2)
      playersMap += new Stat(name, games.size, games.sum, games.sum/games.size, games.sum/games.size * 100 + "%")
    })
    val rating = playersMap.filter(_.count > 50).sortBy(_.kef).reverse.zipWithIndex
    println(s"[spoiler=Рейтинг первых посадок]")

   println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Ник[/th][th]Число игр[/th][th]Число посадок[/th][th]Процент посадок[/th][/tr]")
    rating.foreach(res => {
      val rate = res._1
      println(s"[tr][td]${res._2 + 1}[/td][td][nick]${rate.name}[/nick][/td][td]${rate.count}[/td][td]${rate.countPris}[/td][td]${rate.kef}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")*/
  }

  def main(args: Array[String]) = {
    //calculateGenericStat()
    val name1 = "KillHips"
    val name2 = "Rogue"
    /*val name1 = "Unstoppable"
    val name2 = "Антон Городецкий"*/
    val a1 = calculatePersonal(name1)
    val a2 = calculatePersonal(name2)
    println(s"[spoiler=$name1 vs $name2 Ожиданка+Кресты]")

   println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Ник[/th][th]Процент побед команды, если сел первым[/th][th]Процент побед команды, если НЕ сел первым[/th][th]Число игр[/th][th]Посадки первым ходом[/th][/tr]")
    println(s"[tr][td][nick]${name1}[/nick][/td][td]${a1._1._2}[/td][td]${a1._2._2}[/td][td]${a1._1._4}[/td][td]${a1._1._3}[/td][/tr]")
    println(s"[tr][td][nick]${name2}[/nick][/td][td]${a2._1._2}[/td][td]${a2._2._2}[/td][td]${a2._1._4}[/td][td]${a2._1._3}[/td][/tr]")
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
    //calculatePersonal("Картошка")
  }
}