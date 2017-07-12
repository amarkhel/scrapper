package com.supergloo

import webscrapper.database.DB
import webscrapper.Location

object OtvetkaApp {
  def main(args:Array[String]) = {
    case class Stat(name:String, countGames:Int =0, countVotes:Int = 0, countOtvetka:Int = 0, percentGames:Double = 0.0, percentVotes:Double=0.0, countMafia:Int=0, percentMafia:Double=0.0)
    val playersMap = new collection.mutable.HashMap[String, Stat]()
    val games = DB().loadGames(2016, Location.SUMRAK, "", 6)
    games.foreach(g => {
      g.players.foreach(pl => {
        
          val stat = playersMap.getOrElse(pl.name, new Stat(pl.name))
          val votes = stat.countVotes + g.statistics.voteStats.countCitizenVotesFrom(pl)
          val otvetka = stat.countOtvetka + g.statistics.countOtvetka(pl)
          val count = stat.countGames + 1
          val percentG = otvetka.toDouble/count
          val percentV = otvetka.toDouble/votes
          val countMafia = if(pl.isMafia) stat.countMafia + g.statistics.countOtvetka(pl) else stat.countMafia
          val percentMafia = countMafia.toDouble / otvetka * 100
          playersMap.update(pl.name, new Stat(pl.name, count, votes, otvetka, percentG, percentV, countMafia, percentMafia))
      })
    })
    val result = playersMap.map(_._2).filter(_.countGames > 15).toSeq.sortBy(_.percentGames).reverse.zipWithIndex
    println(s"[spoiler=Рейтинг ответок Общий]")

   println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Ник[/th][th]Число игр[/th][th]Число дневных ходов[/th][th]Число ответок[/th][th]Процент ответок за игру[/th][th]Процент ответок по ходам[/th][th]Процент ответок при роли мафия[/th][/tr]")
    result.foreach(res => {
      val rate = res._1
      println(s"[tr][td]${res._2 + 1}[/td][td][nick]${rate.name}[/nick][/td][td]${rate.countGames}[/td][td]${rate.countVotes}[/td][td]${rate.countOtvetka}[/td][td]${rate.percentGames}[/td][td]${rate.percentVotes}[/td][td]${rate.percentMafia}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
}