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

object WordsAppGeneral {

  val directory = "stat2016full/"
  case class Stat(name1:String, name2:String, percent:Double, common:Int, union:Double)
  def even[A](l: Array[A]) = l.zipWithIndex.collect { case (e, i) if ((i + 1) % 2) == 0 => e }
  def odd[A](l: Array[A]) = l.zipWithIndex.collect { case (e, i) if ((i + 1) % 2) == 1 => e }

  def parsePlayers(players: String) = {
    val arr = players.split(",").map(_.replaceAll("name =", "")).map(_.replaceAll("role =", "")).map(_.trim).map(_.replaceAll("Любит на 99,9", "Любит на 99"))
    val ev = even(arr)
    val od = odd(arr)
    val zipped = od.zip(ev).map(e => e._1 -> Role.getByRoleName(e._2))
    zipped
  }
  
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
  
  def parseGamesLoaded() = {
    val games = DB().loadPlayers(2016).map(_._1).flatMap(parsePlayers(_)).map(_._1)
    val players = games.groupBy(identity).map(p => p._1 -> p._2.size).filter(_._2 > 50).map(_._1)
    players
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
  
  def dumpPlayers = {
    
  }
  
  def calculatePersonal(name1:String, name2:String) = {
    val messages1 = Source.fromFile(new File(directory + name1 + ".txt")).getLines().toList.take(200).map(_.split(",")(0))
      val messages2 = Source.fromFile(new File(directory + name2 + ".txt")).getLines().toList.take(200).map(_.split(",")(0))
      val inter = messages1.intersect(messages2).size
      val union = messages1.union(messages2).size.toDouble
      val jSimilarity = inter / union
      println(jSimilarity)
    println(messages1.intersect(messages2))
  }
  
  def calculatePersonal2(name1:String, name2:String,name3:String,name4:String,name5:String,name6:String,name7:String,name8:String,name9:String,name10:String,name11:String) = {
    
    val messages1 = Source.fromFile(new File(directory + name1 + ".txt")).getLines().toList.map(_.split(",")(0))
      val messages2 = Source.fromFile(new File(directory + name2 + ".txt")).getLines().toList.map(_.split(",")(0))
      val messages3 = Source.fromFile(new File(directory + name3 + ".txt")).getLines().toList.map(_.split(",")(0))
      val messages4 = Source.fromFile(new File(directory + name4 + ".txt")).getLines().toList.map(_.split(",")(0))
      val messages5 = Source.fromFile(new File(directory + name5 + ".txt")).getLines().toList.map(_.split(",")(0))
      val messages6 = Source.fromFile(new File(directory + name6 + ".txt")).getLines().toList.map(_.split(",")(0))
      val messages7 = Source.fromFile(new File(directory + name7 + ".txt")).getLines().toList.map(_.split(",")(0))
      val messages8 = Source.fromFile(new File(directory + name8 + ".txt")).getLines().toList.map(_.split(",")(0))
      val messages9 = Source.fromFile(new File(directory + name9 + ".txt")).getLines().toList.map(_.split(",")(0))
      val messages10 = Source.fromFile(new File(directory + name10 + ".txt")).getLines().toList.map(_.split(",")(0))
      val messages11 = Source.fromFile(new File(directory + name11 + ".txt")).getLines().toList.map(_.split(",")(0))
      val inter = messages1.intersect(messages2).intersect(messages3).intersect(messages4).intersect(messages5).intersect(messages6).intersect(messages7).intersect(messages8).intersect(messages9).intersect(messages10).intersect(messages11)
    println(inter)
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
  def calculateGeneral = {
    val res = parseGames
    val games = res._2
    val players = res._1.toList.sortBy(identity)
    players.par.foreach(p => {
      if (!Files.exists(Paths.get(directory + p + ".txt"))) {
        val pl = new Player(Role.BOSS, FinishStatus.ALIVE, p)
        val g = games.filter(_._2.contains(p)).flatMap(_._3).filter(_.from(pl))
        val words = g.map(_.text).flatMap(_.split(" "))
        val mesMap = words.groupBy(identity).map(f => f._1 -> f._2.size).filter(_._2 > 3)
        if (mesMap.size > 200) {
          val top = ListMap(mesMap.toSeq.sortWith(_._2 > _._2): _*).take(400)
          println("messages are filtered for " + pl)
          val res = top.map(_._1).toSet
          println(res.size)
          val file = new File(directory + p + ".txt")
          val bw = new BufferedWriter(new FileWriter(file))
          for (line <- res) bw.write(line + "\n")
          bw.close()
        } else {
          println(s"$p have only ${mesMap.size} different words. Skipped")
        }

      }
    })
    val plPairs = pairs(players)
    val stat = plPairs.par.filter(p => (Files.exists(Paths.get(directory + p._1 + ".txt")) && Files.exists(Paths.get(directory + p._2 + ".txt")))).map(p => {
      val name1 = p._1
      val name2 = p._2
      val messages1 = Source.fromFile(new File(directory + name1 + ".txt")).getLines().toList.map(_.split(",")(0))
      val messages2 = Source.fromFile(new File(directory + name2 + ".txt")).getLines().toList.map(_.split(",")(0))
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
    m.groupBy(identity).map(f => f._1 -> f._2.size).toList.sortBy(_._2).reverse.foreach(println)
  }
  def main(args: Array[String]) = {
    
    calculateGeneral
    //calculateLoaded
    //calculatePersonal("Inn-Ah", "apaapaapa")
    //calculatePersonal2("Inn-Ah", "BlueeyesKitten", "apaapaapa", "Jerry Tough", "гребля", "Luksera", "takotorayaeva", "Faunteleroy", "Rogue", "Kolchuginka", "De Mari")
  }
}