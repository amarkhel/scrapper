package com.supergloo

import webscrapper.database.DB
import webscrapper.Location
import webscrapper.util.StatUtils
import webscrapper.TournamentResult
import scala.collection.mutable.HashMap
import webscrapper.Role
import scala.collection.immutable.ListMap

object KomissarApp {
  
  def main(args:Array[String]) = {
    //val playersMap = new collection.mutable.HashMap[String, Stat]()
    val games = DB().loadGames(year=2016, loc=Location.SUMRAK, minCount = 7)
    val filtered = games.filter(_.statistics.firstKomissarRound.isDefined).map(g => {
        val role = g.statistics.firstKomissarRound.get.checkedRole
        val name = g.statistics.playersInRole(_.isKomissar)(0).name
        (name, role)
    })
    val data = filtered.groupBy(_._1).map{case (k,v) => k -> v.map(_._2)}
    case class Stat(name:String, countChecked:Int, countMafia:Int, countBoss:Int, countManiac:Int, countDoctor:Int, countMiss:Int, countChild:Int, count:Int, kefMafia:Double)
    val calculated = data.map(d => {
      val roles = d._2
      
      val filt = roles.map(_.filter(_ != "Комиссар посажен на первом ходу"))
      val doctor = filt.filter(_ == Role.DOCTOR.role).size
      val child = filt.filter(r => r == Role.CHILD.role || r == Role.CHILD_GIRL.role || r == Role.CHILD_UNKNOWN.role).size
      val maniac = filt.filter(_ == Role.MANIAC.role).size
      val maf = filt.filter(_ == Role.MAFIA.role).size
      val boss = filt.filter(_ == Role.BOSS.role).size
      
      val checks = filt.size
      val missed = roles.size - filt.size
      val kef = (maf + boss).toDouble / checks * 100
      new Stat(d._1, checks, maf, boss, maniac, doctor, missed, child, roles.size, kef)
    })
    val sorted = calculated.filter(_.count > 20).toSeq.sortBy(_.kefMafia).reverse.zipWithIndex
    println(s"[spoiler=Статистика игры за кома 2016(количество проверок)]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Игрок[/th][th]Коэффициент проверок мафии и босса[/th][th]Количество проверок[/th][th]Количество игр[/th][th]Мафия[/th][th]Босс[/th][th]Маньяков[/th][th]Доктор[/th][th]Ребенок[/th][/tr]")
    sorted.foreach(r => {
      println(s"[tr][td]${r._2 + 1}[/td][td][nick]${r._1.name}[/nick][/td][td]${r._1.kefMafia}[/td][td]${r._1.countChecked}[/td][td]${r._1.count}[/td][td]${r._1.countMafia}[/td][td]${r._1.countBoss}[/td][td]${r._1.countManiac}[/td][td]${r._1.countDoctor}[/td][td]${r._1.countChild}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
}