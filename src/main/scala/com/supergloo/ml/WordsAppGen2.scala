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
import webscrapper.Game
import webscrapper.Message

object WordsAppGen2 {

  val directory = "stat2016full/"
  case class Stat(name1:String, name2:String, percent:Double, common:Int, union:Double)
  def even[A](l: Array[A]) = l.zipWithIndex.collect { case (e, i) if ((i + 1) % 2) == 0 => e }
  def odd[A](l: Array[A]) = l.zipWithIndex.collect { case (e, i) if ((i + 1) % 2) == 1 => e }

  def parsePlayers(players: String) = {
    val arr = players.split(",").map(_.replaceAll("name =", "")).map(_.replaceAll("role =", "")).map(_.trim).map(_.replaceAll("Любит на 99,9", "Любит на 99"))
    val ev = even(arr)
    val od = odd(arr)
    od.zip(ev).map(e => e._1 -> Role.getByRoleName(e._2))
  }
  
  def parseGamesLoaded() = {
    val games = DB().loadPlayers(2016).map(_._1).flatMap(parsePlayers(_)).map(_._1)
    val players = games.groupBy(identity).map(p => p._1 -> p._2.size).filter(_._2 > 50).map(_._1)
    players
  }

  def pairs(list:List[String]) = {
    val combinations = for(x <- list; y <- list) yield (x, y)
    combinations.filter(c => c._1 != c._2)
  }
  
  def calculateLoaded = {
    val res = parseGamesLoaded.toList.sortBy(identity)
    val plPairs = pairs(res)
    val stat = plPairs.par.filter(p => (Files.exists(Paths.get(directory + p._1 + ".txt")) && Files.exists(Paths.get(directory + p._2 + ".txt")))).map(p => {
      val name1 = p._1
      val name2 = p._2
      val messages1 = Source.fromFile(new File(directory + name1 + ".txt")).getLines().toList.take(200).map(_.split(",")(0))
      val messages2 = Source.fromFile(new File(directory + name2 + ".txt")).getLines().toList.take(200).map(_.split(",")(0))
      val inter = messages1.intersect(messages2).size
      val union = messages1.union(messages2).size.toDouble
      val jSimilarity = inter / union
      new Stat(name1, name2, jSimilarity, inter, union)
    }).toSeq
    
    val result = stat.toList.sortBy(_.percent).reverse.zipWithIndex
    println(s"[spoiler=Статистика одинаково общаюшихся]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Ник[/th][th]Ник[/th][th]Процент схожести общения[/th][th]Общих слов[/th][th]Совокупных слов[/th][/tr]")
    result.foreach(res => {
      println(s"[tr][td]${res._2 + 1}[/td][td][nick]${res._1.name1}[/nick][/td][td][nick]${res._1.name2}[/nick][/td][td]${res._1.percent}[/td][td]${res._1.common}[/td][td]${res._1.union}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
    printTopWords(result)
  }

  def printTopWords(result: List[(Stat, Int)]) = {
    val top = result.take(100)
    val nicks1 = top.map(_._1.name1)
    val nicks2 = top.map(_._1.name2)
    val nicks = nicks1.union(nicks2)
    val groupped = nicks.groupBy(identity).map(c => c._1 -> c._2.size).toList.sortBy(_._2).reverse.take(5).map(_._1)
    groupped.foreach(println)
    val nePairs = top.filter(t => groupped.contains(t._1.name1) || groupped.contains(t._1.name2)).map(d => d._1.name1 -> d._1.name2)
    val m = nePairs.flatMap(n => {
      val name1 = n._1
      val name2 = n._2
      val messages1 = Source.fromFile(new File(directory + name1 + ".txt")).getLines().toList.map(_.split(",")(0))
      val messages2 = Source.fromFile(new File(directory + name2 + ".txt")).getLines().toList.map(_.split(",")(0))
      val inter = messages1.intersect(messages2)
      inter
    })
    m.groupBy(identity).map(f => f._1 -> f._2.size).toList.sortBy(_._2).reverse.map(_._1).foreach(println)
  }
  
  def filterMessages(games:List[Game], op: Message => String) = {
    games.par.map(g => {
      for(
          player <- g.players;
          messages = g.statistics.messageStats.from(player);
          mapped = messages.map(op).filter(!_.trim.isEmpty).mkString(" ");
          if(mapped != Nil);
          role = player.role;
          label = if(player.isMafia) "0" else "1"
            
      ) yield player.name -> (label + ", " + mapped)
    }).flatMap(identity).groupBy(_._1).map(p => p._1 -> p._2.map(_._2).seq).seq.filter(_._2.size > 100).toMap
  }
  
  def calculateGeneral = {
    val games = DB().loadGames(2016, Location.SUMRAK, "", 7)
    println("games loaded")
    writeMapTo(directory, filterMessages(games, _.toStem), "stemmed")
    writeMapTo(directory, filterMessages(games, _.rem))
  }
  
  def writeMapTo(directory:String, map:Map[String, Seq[String]], postfix:String="",  op: String=>String = s => s + "\n") = {
    map.foreach(m => {
      if (!Files.exists(Paths.get(directory + m._1 + "_" + postfix + ".txt"))) {
          val file = new File(directory + m._1 + postfix + ".txt")
          val bw = new BufferedWriter(new FileWriter(file))
          for (line <- m._2) bw.write(op(line))
          bw.close()
      }
    })
  }
  
  def main(args: Array[String]) = {
    calculateGeneral
  }
}