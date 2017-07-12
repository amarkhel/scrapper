package com.supergloo

import webscrapper.database.DB
import webscrapper.Location
import webscrapper.util.StatUtils
import webscrapper.TournamentResult
import scala.collection.mutable.HashMap
import webscrapper.Role
import scala.collection.immutable.ListMap
import scala.collection.mutable.ListBuffer


object FirstMafiaKillApp {
  
  case class Stat(name:String, count:Int=0, countKill:Int=0, countKom:Int =0, kef:Double =0.0, countSerzh:Int = 0, countDoctor:Int = 0, countManiac:Int = 0)
  def main(args: Array[String]) = {
    //val playersMap = new collection.mutable.HashMap[String, Stat]()
    val games = DB().loadGames(year=2016, loc=Location.OZHA, minCount = 7)
    val filtered = games.filter(_.statistics.firstMafiaRound.isDefined).flatMap(g => {
      var role =""
      var name = ""
      if(g.statistics.firstMafiaKill.isDefined) {
        role = g.statistics.firstMafiaKill.get.role.role
        name = g.statistics.firstMafiaKill.get.name
      }
      val gamers = g.players.filter(g.statistics.firstMafiaRound.get.alived(_)).map(pl => {
        val playerRole = pl.role.role
        val playerName = pl.name
        val killed = pl.name == name
        (playerName, playerRole, killed)
      })
      gamers
    })
    val data = filtered.groupBy(_._1).map{case (k,v) => k -> v.map(a => (a._2, a._3))}
    val calculated = data.map(d => {
      val roles = d._2
      
      val filt = roles.filter(_._2)
      val kom = filt.filter(_._1 == Role.KOMISSAR.role).size
      val serzh = filt.filter(_._1 == Role.SERZHANT.role).size
      val doc = filt.filter(_._1 == Role.DOCTOR.role).size
      val man = filt.filter(_._1 == Role.MANIAC.role).size
      
      val moroz = filt.size
      val kef = moroz.toDouble / roles.size * 100
      new Stat(d._1, roles.size, moroz, kom, kef, serzh, doc, man)
    })
    val sorted = calculated.filter(_.count > 20).toSeq.sortBy(_.kef).reverse.zipWithIndex
    println(s"[spoiler=Статистика первого убийства мафией 2016 Ожиданка]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Игрок[/th][th]Процент первого убийства мафией[/th][th]Количество убийств первым ходом[/th][th]Количество игр[/th][th]Ком[/th][th]Серж[/th][th]Док[/th][th]Ман[/th][/tr]")
    sorted.foreach(r => {
      println(s"[tr][td]${r._2 + 1}[/td][td][nick]${r._1.name}[/nick][/td][td]${r._1.kef}[/td][td]${r._1.countKill}[/td][td]${r._1.count}[/td][td]${r._1.countKom}[/td][td]${r._1.countSerzh}[/td][td]${r._1.countDoctor}[/td][td]${r._1.countManiac}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
}