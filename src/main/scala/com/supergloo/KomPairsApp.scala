package com.supergloo

import webscrapper.database.DB
import webscrapper.Location
import scala.collection.mutable.ListMap
import scala.collection.immutable.TreeMap
import webscrapper.TournamentResult

object KomPairsApp {
  def pairsStat = {
      val games = DB().loadGames(year=2016, loc=Location.OZHA, minCount = 7)
      val filtered = games.flatMap(_.statistics.komPairs)
      val grouped = filtered.groupBy(identity).map(g => g._1 -> g._2.size).filter(_._2 > 3)
      val sorted = grouped.map(x =>(x._1._1, x._1._2, x._2)).toSeq.sortBy(_._3).reverse.zipWithIndex

      println(s"[spoiler=Статистика самых частых напарников-комов]")
      println(s"[table]")
      println(s"[tbody]")
      println(s"[tr][th]Место[/th][th]Игрок 1[/th][th]Игрок 2[/th][th]Количество игр вместе[/th][/tr]")
      sorted.foreach(r => {
        println(s"[tr][td]${r._2 + 1}[/td][td][nick]${r._1._1}[/nick][/td][td][nick]${r._1._2}[/nick][/td][td]${r._1._3}[/td][/tr]")
      })
      println(s"[/tbody]")
      println(s"[/table]")
      println(s"[/spoiler]")
    }
  
  def winPoints(res:TournamentResult) = {
    res match {
      case TournamentResult.GOROD_WIN => 1.0
      case TournamentResult.MAFIA_WIN => 0.0
      case TournamentResult.OMON_1 => 0.5
      case TournamentResult.OMON_2 => 0.5
      case TournamentResult.OMON_3 => 0.5
      case TournamentResult.OMON_4 => 0.5
    }
  }
  def pairsStatWin = {
      val games = DB().loadGames(year=2016, loc=Location.OZHA, minCount = 7)
      val filtered = games.flatMap(g => {
        val pairs = g.statistics.komPairs
        val res = g.tournamentResult
        for (pair <- pairs) yield ((pair._1, pair._2), winPoints(res))
      })
      val grouped = filtered.groupBy(_._1).map(c => c._1 -> c._2.map(_._2)).filter(_._2.size > 3)
      val sorted = grouped.map(x =>(x._1._1, x._1._2, x._2.size, x._2.sum)).toSeq.sortBy(_._4).reverse.zipWithIndex

      println(s"[spoiler=Статистика самых частых напарников-комов процент побед]")
      println(s"[table]")
      println(s"[tbody]")
      println(s"[tr][th]Место[/th][th]Игрок 1[/th][th]Игрок 2[/th][th]Количество игр вместе[/th][th]Количество побед[/th][th]Процент побед[/th][/tr]")
      sorted.foreach(r => {
        println(s"[tr][td]${r._2 + 1}[/td][td][nick]${r._1._1}[/nick][/td][td][nick]${r._1._2}[/nick][/td][td]${r._1._3}[/td][td]${r._1._4}[/td][td]${r._1._4.toDouble / r._1._3}[/td][/tr]")
      })
      println(s"[/tbody]")
      println(s"[/table]")
      println(s"[/spoiler]")
    }
  
  def main(args: Array[String]) = {
    
    pairsStat
    pairsStatWin
  }
}