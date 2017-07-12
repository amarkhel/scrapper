package com.supergloo

import scala.collection.mutable.ListBuffer
import webscrapper.Round
import webscrapper.Player
import webscrapper.Role
import webscrapper.Message
import webscrapper.database.DB
import webscrapper.Location

object MessageDistributionApp {
  
  val games = DB().loadGames(2016, Location.SUMRAK, "", 6)
  
  def mapToSize[A, B](map:collection.Map[A, List[B]]) = map.map { case (key, value) => (key, value.size)}
  
  def firstMessageOrderDistribution = messageOrderDistribution(1)
  
  def secondMessageOrderDistribution = messageOrderDistribution(2)
  
  def thirdMessageOrderDistribution = messageOrderDistribution(3)
  
  def timeBetweenMessagesDistribution = timeMessageDistribution(_.diffBetweenFirstAndSecondMessages(_))
  
  def timeBeforeFirstMessageDistribution = timeMessageDistribution(_.messageNumberXTime(1, _))
  
  def timeBeforeSecondMessageDistribution = timeMessageDistribution(_.messageNumberXTime(2, _))
  
  def timeBeforeThirdMessageDistribution = timeMessageDistribution(_.messageNumberXTime(3, _))
  
  def timeBefore4MessageDistribution = timeMessageDistribution(_.messageNumberXTime(4, _))
  
  private def messageOrderDistribution(order:Int) = {
    val result = messageXOrderDistribution(_.messageNumberXFrom(order, _))
    val mafia = mapToSize(result(Role.MAFIA).groupBy {identity})
    val citizen = mapToSize(result(Role.CITIZEN).groupBy {identity})
    val kom = mapToSize(result(Role.KOMISSAR).groupBy {identity})
    val boss = mapToSize(result(Role.BOSS).groupBy {identity})
    val doctor = mapToSize(result(Role.DOCTOR).groupBy {identity})
    val maniac = mapToSize(result(Role.MANIAC).groupBy {identity})
    val serzh = mapToSize(result(Role.SERZHANT).groupBy {identity})
    val child = mapToSize(result(Role.CHILD).groupBy {identity})
    (mafia, boss, kom, serzh, maniac, doctor, child, citizen)
  }
  
    private def timeMessageDistribution(f: (Round,Player) => Option[Int]) = {
    val distribution = ListBuffer[(Role, Int)]()
    games.foreach(game => {
      val firstRound = game.statistics.firstRound.get
      val times = for(player <- game.players if(f(firstRound, player) != None)) 
        yield player.role -> f(firstRound, player).get
      distribution ++= times
    }
    )
    distribution.groupBy(_._1).map{case (k,v) => k -> v.foldLeft(0)(_+_._2).toDouble / v.size}
  }
  
  private def messageXOrderDistribution(f: (Round,Player) => Option[Message]) = {
    val distribution = ListBuffer[(Role, Int)]()
    games.foreach(game => {
      val firstRound = game.statistics.firstRound.get
      val firstMessages = for(player <- game.players if(f(firstRound, player) != None)) 
        yield firstRound.firstMessageFrom(player)
      val sortedMessages = firstMessages.sortBy(_.get.timeFromStart)
      distribution ++= sortedMessages.zipWithIndex.map{case (k, v) => k.get.fromRole -> (v + 1)}
    }
    )
    distribution.groupBy(_._1).map{case (k,v) => k -> v.foldLeft(ListBuffer[Int]())(_+=_._2).toList}
  }
  
  def main(args: Array[String]) = {
    
   /*println(firstMessageOrderDistribution)
   println(secondMessageOrderDistribution)
   println(thirdMessageOrderDistribution)*/
   var t1 = timeBetweenMessagesDistribution
   println(s"[spoiler=Статистика времени между первым и вторым сообщениями сообщений]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Роль[/th][th]Время с начала первого раунда[/th][/tr]")
    t1.foreach(res => {
      println(s"[tr][td]${res._1.role}[/td][td]${res._2}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
   t1 = timeBeforeFirstMessageDistribution
   println(s"[spoiler=Статистика времени первых сообщений]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Роль[/th][th]Время с начала первого раунда[/th][/tr]")
    t1.foreach(res => {
      println(s"[tr][td]${res._1.role}[/td][td]${res._2}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
   t1 = timeBeforeSecondMessageDistribution
   println(s"[spoiler=Статистика времени вторых сообщений]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Роль[/th][th]Время с начала первого раунда[/th][/tr]")
    t1.foreach(res => {
      println(s"[tr][td]${res._1.role}[/td][td]${res._2}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
   t1 = timeBeforeThirdMessageDistribution
   println(s"[spoiler=Статистика времени третьих сообщений]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Роль[/th][th]Время с начала первого раунда[/th][/tr]")
    t1.foreach(res => {
      println(s"[tr][td]${res._1.role}[/td][td]${res._2}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
}