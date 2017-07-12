package com.supergloo

import webscrapper.database.DB
import webscrapper.Location
import scala.collection.mutable.ListBuffer
import webscrapper.Role

object PersonalFirstNightApp {
  def main(args:Array[String]) = {
    calculatePersonal("Frozen")
    calculatePersonal("Hipnos")
    calculatePersonal("фрозен")
    
    calculatePersonal2("Вилли Токарев")
  }
  
  def calculatePersonal(nick:String) = {
    val games = DB().loadGames(year=2016, loc=Location.OZHA, minCount = 7, name=nick)
    val firstKilled = ListBuffer[Long]()
    val firstKilledMan = ListBuffer[Long]()
    val firstTried = ListBuffer[Long]()
    games.foreach(g => {
      if(g.statistics.doctorAttemptedFirstRoundPlayer.isDefined){
        val name = g.statistics.doctorAttemptedFirstRoundPlayer.get.name
        if(name == nick){
          firstTried += g.id
        }
      }
      if(g.statistics.firstMafiaKill.isDefined){
        val name = g.statistics.firstMafiaKill.get.name
        if(name == nick){
          firstKilled += g.id
        }
      }
      if(g.statistics.firstManiacKill.isDefined){
        val name = g.statistics.firstManiacKill.get.name
        if(name == nick){
          firstKilledMan += g.id
        }
      }
    })
    println(s"[spoiler=Статистика кто больше всего лечил " + nick +  "]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Номер партии[/th][/tr]")
    firstTried.foreach(r => {
      println(s"[tr][td][log]${r}[/log][/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
    println(s"[spoiler=Статистика убтйств маньяком " + nick +  "]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Номер партии[/th][/tr]")
    firstKilledMan.foreach(r => {
      println(s"[tr][td][log]${r}[/log][/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
    println(s"[spoiler=Статистика убийств мафией " + nick +  "]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Номер партии[/th][/tr]")
    firstKilled.foreach(r => {
      println(s"[tr][td][log]${r}[/log][/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
  
  def calculatePersonal2(nick:String) = {
    val games = DB().loadGames(year=2016, loc=Location.OZHA, minCount = 12, name=nick)
    val firstKilled = ListBuffer[Long]()
    val firstKilledMan = ListBuffer[Long]()
    games.foreach(g => {
      if(g.statistics.firstMafiaKill.isDefined){
        val name = g.statistics.firstMafiaKill.get.name
        if(name == "rimus" && g.findPlayer(nick).get.role == Role.MAFIA){
          firstKilled += g.id
        }
      }
      if(g.statistics.firstManiacKill.isDefined){
        val name = g.statistics.firstManiacKill.get.name
        if(name == "rimus" && g.findPlayer(nick).get.role == Role.MANIAC){
          firstKilled += g.id
        }
      }
    })
    
    println(s"[spoiler=Статистика убтйств маньяком " + nick +  "]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Номер партии[/th][/tr]")
    firstKilledMan.foreach(r => {
      println(s"[tr][td][log]${r}[/log][/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
    println(s"[spoiler=Статистика убийств мафией " + nick +  "]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Номер партии[/th][/tr]")
    firstKilled.foreach(r => {
      println(s"[tr][td][log]${r}[/log][/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
}