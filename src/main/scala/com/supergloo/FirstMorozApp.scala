package com.supergloo

import webscrapper.database.DB
import webscrapper.Location
import webscrapper.util.StatUtils
import webscrapper.TournamentResult
import scala.collection.mutable.HashMap
import webscrapper.Role
import scala.collection.immutable.ListMap
import scala.collection.mutable.ListBuffer


object FirstMorozApp {
  
  case class Stat(name:String, count:Int=0, countFreeze:Int=0, countKom:Int =0, kef:Double =0.0, countSerzh:Int = 0, countDoctor:Int = 0, countManiac:Int = 0)
  def main(args: Array[String]) = {
    //val playersMap = new collection.mutable.HashMap[String, Stat]()
    val games = DB().loadGames(year=2016, loc=Location.OZHA, minCount = 12)
    val filtered = games.filter(_.statistics.firstBossRound.isDefined).flatMap(g => {
      var role =""
      var name = ""
      if(g.statistics.firstMorozRole.isDefined && g.statistics.firstMoroz.isDefined) {
        role = g.statistics.firstMorozRole.get.role
        name = g.statistics.firstMoroz.get
      }
      val gamers = g.players.filter(g.statistics.firstBossRound.get.alived(_)).map(pl => {
        val playerRole = pl.role.role
        val playerName = pl.name
        val morozed = pl.name == name
        (playerName, playerRole, morozed)
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
    println(s"[spoiler=Статистика первого мороза 2016]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Игрок[/th][th]Процент первого мороза[/th][th]Количество морозов[/th][th]Количество игр[/th][th]Ком[/th][th]Серж[/th][th]Док[/th][th]Ман[/th][/tr]")
    sorted.foreach(r => {
      println(s"[tr][td]${r._2 + 1}[/td][td][nick]${r._1.name}[/nick][/td][td]${r._1.kef}[/td][td]${r._1.countFreeze}[/td][td]${r._1.count}[/td][td]${r._1.countKom}[/td][td]${r._1.countSerzh}[/td][td]${r._1.countDoctor}[/td][td]${r._1.countManiac}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
}