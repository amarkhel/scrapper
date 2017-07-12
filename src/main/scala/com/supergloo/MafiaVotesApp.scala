package com.supergloo

import webscrapper.database.DB
import webscrapper.Location
import scala.collection.mutable.ListBuffer

object MafiaVotesApp {
  def main(args:Array[String]) = {
    case class Stat(name:String, countGames:Int =0, countVotes:Int = 0, countMafia:Int = 0, percentGames:Double = 0.0, percentVotes:Double=0.0)
    val playersMap = new collection.mutable.HashMap[String, Stat]()
    implicit val year = 2016
    implicit val location = Location.SUMRAK
    implicit val header = "Рейтинг ходов в мафию за честных"
    val games = DB().loadGames(year, location, "", 6)
    games.foreach(g => {
      g.players.foreach(pl => {
        if(!pl.isMafia && !pl.isManiac && g.statistics.citizenRoundsBeforePrisonedVotes(pl) != Nil){
          val stat = playersMap.getOrElse(pl.name, new Stat(pl.name))
          val votes = stat.countVotes + g.statistics.citizenRoundsBeforePrisonedVotes(pl).size
          val alibi = stat.countMafia + g.statistics.citizenRoundsBeforePrisonedVotes(pl).filter(_.hasVoteToMafiaFrom(pl)).size
          val count = stat.countGames + 1
          val percentG = alibi.toDouble/count
          val percentV = alibi.toDouble/votes
          playersMap.update(pl.name, new Stat(pl.name, count, votes, alibi, percentG, percentV))
        }
      })
    })
    val rating = playersMap.map(_._2).filter(_.countGames > 30).toSeq.sortBy(_.percentGames).reverse.zipWithIndex
    val headers = new ListBuffer[(String, Boolean, (Stat, Int) => Any)]
    headers += ("Место", false, (stat, rate) => rate)
    headers += ("Ник", true, (stat, rate) => stat.name)
    headers += ("Число игр", false, (stat, rate) => stat.countGames)
    headers += ("Число дневных ходов", false, (stat, rate) => stat.countVotes)
    headers += ("Число ходов в мафию", false, (stat, rate) => stat.countMafia)
    headers += ("Ходов за игру в мафию", false, (stat, rate) => stat.percentGames)
    headers += ("Процент ходов в мафию", false, (stat, rate) => stat.percentVotes * 100)
    Util.printTable[Stat](rating.toList, headers.toList)
  }
}