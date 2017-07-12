package com.supergloo

import webscrapper.Location
import webscrapper.util.StatUtils
import webscrapper.TournamentResult
import scala.collection.mutable.HashMap
import webscrapper.Role
import scala.collection.immutable.ListMap
import webscrapper.database.DB

object DoctorApp {
  
  def main(args:Array[String]) = {
    //val playersMap = new collection.mutable.HashMap[String, Stat]()
    val games = DB().loadGames(year=2016, loc=Location.OZHA, minCount = 12)
    val filtered = games.map(g => {
      val role = g.statistics.doctorSaved
      val name = g.statistics.playersInRole(_.isDoctor)(0).name
      (name, role)
    })
    val data = filtered.groupBy(_._1).map{case (k,v) => k -> v.map(_._2)}
    case class Stat(name:String, countSaved:Int, countKom:Int, countSerzh:Int, countManiac:Int, countMafia:Int, countBoss:Int, count:Int, kef:Double)
    val calculated = data.map(d => {
      val roles = d._2
      
      val filt = roles.map(_.filter(_.isDefined).map(_.get)).flatMap(_.toList)
      val kom = filt.filter(_ == Role.KOMISSAR).size
      val serzh = filt.filter(_ == Role.SERZHANT).size
      val maniac = filt.filter(_ == Role.MANIAC).size
      val maf = filt.filter(_ == Role.MAFIA).size
      val boss = filt.filter(_ == Role.BOSS).size
      val kef = filt.size.toDouble / roles.size
      val saved = filt.size
      new Stat(d._1, saved, kom, serzh, maniac, maf, boss, roles.size, kef)
    })
    val sorted = calculated.filter(_.count > 4).toSeq.sortBy(_.kef).reverse.zipWithIndex
    println(s"[spoiler=Статистика игры за врача 2016(количество вылеченных)]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Игрок[/th][th]Коэффициент[/th][th]Количество вылеченных[/th][th]Количество игр[/th][th]Комиссаров[/th][th]Сержантов[/th][th]Маньяков[/th][th]Мафия[/th][th]Босс[/th][/tr]")
    sorted.foreach(r => {
      println(s"[tr][td]${r._2 + 1}[/td][td][nick]${r._1.name}[/nick][/td][td]${r._1.kef}[/td][td]${r._1.countSaved}[/td][td]${r._1.count}[/td][td]${r._1.countKom}[/td][td]${r._1.countSerzh}[/td][td]${r._1.countManiac}[/td][td]${r._1.countMafia}[/td][td]${r._1.countBoss}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
}