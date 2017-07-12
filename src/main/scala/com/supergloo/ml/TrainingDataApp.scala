package com.supergloo.ml

import webscrapper.database.DB
import webscrapper.Location
import webscrapper.Role
import webscrapper.FinishStatus
import webscrapper.Player
import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import scala.collection.mutable.ListBuffer
import scala.io.Source

object TrainingDataApp {

  val directory = "stat2016full/overall.txt"
  
  def loadMessagesWithRole = {
    val games = DB().loadGames(2016, Location.SUMRAK, "", 7)
    
    val parsed = games.par.flatMap(g => {
      val mess = for(
          player <- g.players;
          messages = g.statistics.messageStats.messages.filter(_.from(player));
          mapped = messages.map(_.toNorm).filter(!_.text.trim.isEmpty).map(_.text).mkString(" ");
          role = player.role;
          label = if(role == Role.MAFIA || role == Role.BOSS) "0" else "1"
      ) yield (label, mapped)
      mess
    }).toList
    parsed
  }
  
  def calculateMafCitMessages() = {
    val res = loadMessagesWithRole
      if (!Files.exists(Paths.get(directory))) {
          val file = new File(directory)
          val bw = new BufferedWriter(new FileWriter(file))
          for (line <- res) bw.write(line._1 + "," + line._2 + "\n")
          bw.close()
      }
  }
  
  def main(args: Array[String]) = {
    calculateMafCitMessages()
  }
}