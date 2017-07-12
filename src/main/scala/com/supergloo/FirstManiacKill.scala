package com.supergloo

import webscrapper.database.DB
import webscrapper.Location
import webscrapper.util.StatUtils
import webscrapper.TournamentResult
import scala.collection.mutable.HashMap
import webscrapper.Role
import scala.collection.immutable.ListMap
import scala.collection.mutable.ListBuffer


object FirstManiacKillApp {
  
  case class Stat(name:String, count:Int=0, countKill:Int=0, countKom:Int =0, kef:Double =0.0, countSerzh:Int = 0, countDoctor:Int = 0, countMaf:Int = 0, countBoss:Int = 0)
  def main(args: Array[String]) = {
    //val playersMap = new collection.mutable.HashMap[String, Stat]()
    val games = DB().loadGames(year=2016, loc=Location.OZHA, minCount = 12)
    val filtered = games.filter(_.statistics.firstManiacRound.isDefined).flatMap(g => {
      var role =""
      var name = ""
      if(g.statistics.firstManiacKill.isDefined) {
        role = g.statistics.firstManiacKill.get.role.role
        name = g.statistics.firstManiacKill.get.name
      }
      val gamers = g.players.filter(g.statistics.firstManiacRound.get.alived(_)).map(pl => {
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
      val maf = filt.filter(_._1 == Role.MAFIA.role).size
      val boss = filt.filter(_._1 == Role.BOSS.role).size
      
      val moroz = filt.size
      val kef = moroz.toDouble / roles.size * 100
      new Stat(d._1, roles.size, moroz, kom, kef, serzh, doc, maf, boss)
    })
    val sorted = calculated.filter(_.count > 20).toSeq.sortBy(_.kef).reverse.zipWithIndex
    println(s"[spoiler=Статистика первого убийства маном 2016]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Игрок[/th][th]Процент первого убийства маном[/th][th]Количество убийств первым ходом[/th][th]Количество игр[/th][th]Ком[/th][th]Серж[/th][th]Док[/th][th]Маф[/th][th]Босс[/th][/tr]")
    sorted.foreach(r => {
      println(s"[tr][td]${r._2 + 1}[/td][td][nick]${r._1.name}[/nick][/td][td]${r._1.kef}[/td][td]${r._1.countKill}[/td][td]${r._1.count}[/td][td]${r._1.countKom}[/td][td]${r._1.countSerzh}[/td][td]${r._1.countDoctor}[/td][td]${r._1.countMaf}[/td][td]${r._1.countBoss}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
}