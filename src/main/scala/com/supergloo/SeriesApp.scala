package com.supergloo

import webscrapper.database.DB
import webscrapper.Location
import webscrapper.util.StatUtils
import webscrapper.TournamentResult
import scala.collection.mutable.HashMap
import webscrapper.Role
import scala.collection.immutable.ListMap

object SeriesApp {
  
  def main(args:Array[String]) = {
    //val playersMap = new collection.mutable.HashMap[String, Stat]()
    val games = DB().loadGames(year=2016, loc=Location.OZHA, minCount = 12)
    val filtered = games.map(g => {
      val role = g.statistics.firstMorozRole
      val name = g.statistics.playersInRole(_.isBoss)(0).name
      (name, role)
    })
    val data = filtered.groupBy(_._1).map{case (k,v) => k -> v.map(_._2)}
    case class Stat(name:String, countKom:Int, countSerzh:Int, countDoctor:Int, countManiac:Int, countMiss:Int, count:Int, possiblePoints:Double, points:Double, kef:Double)
    val calculated = data.map(d => {
      val roles = d._2
      
      val filt = roles.filter(_.isDefined).map(_.get)
      val missed = roles.size - filt.size
      val kom = filt.filter(_ == Role.KOMISSAR).size
      val serzh = filt.filter(_ == Role.SERZHANT).size
      val doctor = filt.filter(_ == Role.DOCTOR).size
      val maniac = filt.filter(_ == Role.MANIAC).size
      val rest = filt.size - kom - serzh - doctor - maniac
      val points = kom * 2.0 + serzh*1.5 + doctor* 1.5 + rest*0.5 - missed*0.5 - maniac * 0.1
      val possiblePoints = roles.size * 2.0
      val kef = points / possiblePoints
      new Stat(d._1, kom, serzh, doctor, maniac, missed, roles.size, possiblePoints, points, kef)
    })
    val sorted = calculated.filter(_.count > 3).toSeq.sortBy(_.kef).reverse.zipWithIndex
    println(s"[spoiler=Статистика игры за босса 2016(морозы)]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Позиция в рейтинге[/th][th]Игрок[/th][th]Коэффициент[/th][th]Количество игр[/th][th]Комиссаров[/th][th]Сержантов[/th][th]Врачей[/th][th]Маньяков[/th][th]Пропуцщено ходов[/th][/tr]")
    sorted.foreach(r => {
      println(s"[tr][td]${r._2 + 1}[/td][td][nick]${r._1.name}[/nick][/td][td]${r._1.kef}[/td][td]${r._1.count}[/td][td]${r._1.countKom}[/td][td]${r._1.countSerzh}[/td][td]${r._1.countDoctor}[/td][td]${r._1.countManiac}[/td][td]${r._1.countMiss}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
}