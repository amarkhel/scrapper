package com.supergloo

import webscrapper.database.DB
import webscrapper.Location
import webscrapper.Role
import webscrapper.FinishStatus
import webscrapper.Player
import scala.collection.immutable.ListMap
import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import scala.collection.mutable.ListBuffer
import scala.io.Source

object WordsTest3App {

  //val directory = "stat2016full/"
  
  def parseGames() = {
    val games = DB().loadGames(2016, Location.SUMRAK, "", 7)
    val parsed = games.par.map(g => {
      val messages = g.statistics.messageStats.messages
      val mapped = messages.map(_.toNorm).filter(!_.text.trim.isEmpty)
      (g.id, g.players.map(_.name), mapped)
    }).toList
    val players = parsed.flatMap(_._2).groupBy(identity).map(p => p._1 -> p._2.size).filter(_._2 > 50).map(_._1)
    val filtered = parsed.par.map(p => {
      val unique = p._2.intersect(players.toSeq)
      val messages = p._3.filter(m => unique.contains(m.author))
      (p._1, unique, messages)
    }).filter(!_._2.isEmpty).filter(!_._3.isEmpty).toList
    
    (players, filtered)
  }
  
  def similarity(t1: Map[String, Int], t2: Map[String, Int]): Double = {
     //word, t1 freq, t2 freq
     val m = scala.collection.mutable.HashMap[String, (Int, Int)]()

     val sum1 = t1.foldLeft(0d) {case (sum, (word, freq)) =>
         m += word ->(freq, 0)
         sum + freq
     }

     val sum2 = t2.foldLeft(0d) {case (sum, (word, freq)) =>
         m.get(word) match {
             case Some((freq1, _)) => m += word ->(freq1, freq)
             case None => m += word ->(0, freq)
         }
         sum + freq
     }

     val (p1, p2, p3) = m.foldLeft((0d, 0d, 0d)) {case ((s1, s2, s3), e) =>
         val fs = e._2
         val f1 = fs._1 / sum1
         val f2 = fs._2 / sum2
         (s1 + f1 * f2, s2 + f1 * f1, s3 + f2 * f2)
     }

     val cos = p1 / (Math.sqrt(p2) * Math.sqrt(p3))
     cos
 }

  def pairs(list:List[String]) = {
    def pairsInn(acc:ListBuffer[(String, String)], players:List[String]) : List[(String, String)] = {
      players match {
        case (head :: tail) => {
          val p = for (maf <- tail) yield (head, maf)
          acc ++=(p)
          pairsInn(acc, tail)
        }
        case _ => acc.toList
      }
    }
    pairsInn(new ListBuffer[(String, String)](), list)
  }
  
  def calculateGeneral = {
    val res = parseGames
    val games = res._2
    val players = res._1.toList.sortBy(identity)
    val map = new collection.mutable.HashMap[String, Map[String, Int]]
    players.par.foreach(p => {
      
        val pl = new Player(Role.BOSS, FinishStatus.ALIVE, p)
        val g = games.filter(_._2.contains(p)).flatMap(_._3).filter(_.from(pl))
        val words = g.map(_.text).flatMap(_.split(" "))
        val mesMap = words.groupBy(identity).map(f => f._1 -> f._2.size).filter(_._2 > 3)
        if (mesMap.size > 20) map.put(p, mesMap)
        /*} else {
          println(s"$p have only ${mesMap.size} different words. Skipped")
        }*/

    })
    val plPairs = pairs(players)
    val res2 = plPairs.filter(a => map.get(a._1).isDefined && map.get(a._2).isDefined).map(f => {
      val map1 = map.get(f._1).get
      val map2 = map.get(f._2).get
      
        println(s"comparing ${f._1} with ${f._2}")
      (f._1, f._2, similarity(map1, map2))
      
    })
    val result = res2.sortBy(_._3).toList.reverse.zipWithIndex
    println(s"[spoiler=Статистика одинаково общаюшихся]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Ник[/th][th]Ник[/th][th]Процент схожести общения[/th][/tr]")
    result.foreach(res => {
      println(s"[tr][td]${res._2 + 1}[/td][td][nick]${res._1._1}[/nick][/td][td][nick]${res._1._2}[/nick][/td][td]${res._1._3}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
   
  }
  def main(args: Array[String]) = {
    calculateGeneral
  }
}