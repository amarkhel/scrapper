package com.supergloo

import webscrapper.database.DB
import webscrapper.Location
import webscrapper.util.StatUtils
import webscrapper.TournamentResult
import scala.collection.mutable.HashMap
import webscrapper.Role
import scala.collection.immutable.ListMap
import scala.collection.mutable.ListBuffer


object FirstCheckApp {
  
  case class Stat(name:String, count:Int=0, countCheck:Int=0, countMafia:Int =0, kef:Double =0.0, countBoss:Int = 0)
  def main(args: Array[String]) = {
    //val playersMap = new collection.mutable.HashMap[String, Stat]()
    val games = DB().loadGames(year=2016, loc=Location.OZHA, minCount = 7)
    val filtered = games.filter(_.statistics.firstKomissarRound.isDefined).flatMap(g => {
      var role =""
      var name = ""
      if(g.statistics.firstKomissarRound.isDefined && g.statistics.firstKomissarRound.get.checkedPlayer.isDefined) {
        role = g.statistics.firstKomissarRound.get.checkedRole
        name = g.statistics.firstKomissarRound.get.checkedPlayer.get.name
      }
      val gamers = g.players.filter(g.statistics.firstKomissarRound.get.alived(_)).map(pl => {
        val playerRole = pl.role.role
        val playerName = pl.name
        val checked = pl.name == name
        (playerName, playerRole, checked)
      })
      gamers
    })
    val data = filtered.groupBy(_._1).map{case (k,v) => k -> v.map(a => (a._2, a._3))}
    val calculated = data.map(d => {
      val roles = d._2
      
      val filt = roles.filter(_._2)
      val maf = filt.filter(_._1 == Role.MAFIA.role).size
      val boss = filt.filter(_._1 == Role.BOSS.role).size
      
      val checks = filt.size
      val kef = checks.toDouble / roles.size * 100
      new Stat(d._1, roles.size, checks, maf, kef, boss)
    })
    val sorted = calculated.filter(_.count > 20).toSeq.sortBy(_.kef).reverse.zipWithIndex
    println(s"[spoiler=Статистика первой проверки 2016]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Игрок[/th][th]Процент первой проверки[/th][th]Количество проверок[/th][th]Количество игр[/th][th]Мафия[/th][th]Босс[/th][/tr]")
    sorted.foreach(r => {
      println(s"[tr][td]${r._2 + 1}[/td][td][nick]${r._1.name}[/nick][/td][td]${r._1.kef}[/td][td]${r._1.countCheck}[/td][td]${r._1.count}[/td][td]${r._1.countMafia}[/td][td]${r._1.countBoss}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
}