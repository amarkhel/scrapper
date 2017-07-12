package com.supergloo

import webscrapper.Location
import webscrapper.util.StatUtils
import webscrapper.TournamentResult
import scala.collection.mutable.HashMap
import webscrapper.Role
import scala.collection.immutable.ListMap
import webscrapper.database.DB

object DoctorTriedApp {
  
  def main(args:Array[String]) = {
    //val playersMap = new collection.mutable.HashMap[String, Stat]()
    val games = DB().loadGames(year=2016, loc=Location.OZHA, minCount = 12)
    val filtered = games.filter(_.statistics.doctorAttemptedFirstRoundPlayer.isDefined).flatMap(g => {
        val role = g.statistics.doctorAttemptedFirstRoundPlayer.get.role
        val name = g.statistics.doctorAttemptedFirstRoundPlayer.get.name
      val gamers = g.players.filter(g.statistics.firstMafiaRound.get.alived(_)).map(pl => {
        val playerRole = pl.role
        val playerName = pl.name
        val tried = pl.name == name
        (playerName, playerRole, tried)
      })
      gamers
    })
    val data = filtered.groupBy(_._1).map{case (k,v) => k -> v.map(f => (f._2, f._3))}
    case class Stat(name:String, countTried:Int, countKom:Int, countSerzh:Int, countManiac:Int, countMafia:Int, countBoss:Int, count:Int, kef:Double)
    val calculated = data.map(d => {
      val roles = d._2
      
      val filt = roles.filter(_._2).map(_._1)
      val kom = filt.filter(_ == Role.KOMISSAR).size
      val serzh = filt.filter(_ == Role.SERZHANT).size
      val maniac = filt.filter(_ == Role.MANIAC).size
      val maf = filt.filter(_ == Role.MAFIA).size
      val boss = filt.filter(_ == Role.BOSS).size
      val kef = filt.size.toDouble / roles.size
      val saved = filt.size
      new Stat(d._1, saved, kom, serzh, maniac, maf, boss, roles.size, kef)
    })
    val sorted = calculated.filter(_.count > 20).toSeq.sortBy(_.kef).reverse.zipWithIndex
    println(s"[spoiler=Статистика 2016(кого больше всего лечили)]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Игрок[/th][th]Коэффициент[/th][th]Количество лечений[/th][th]Количество игр[/th][th]Комиссаров[/th][th]Сержантов[/th][th]Маньяков[/th][th]Мафия[/th][th]Босс[/th][/tr]")
    sorted.foreach(r => {
      println(s"[tr][td]${r._2 + 1}[/td][td][nick]${r._1.name}[/nick][/td][td]${r._1.kef}[/td][td]${r._1.countTried}[/td][td]${r._1.count}[/td][td]${r._1.countKom}[/td][td]${r._1.countSerzh}[/td][td]${r._1.countManiac}[/td][td]${r._1.countMafia}[/td][td]${r._1.countBoss}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
}