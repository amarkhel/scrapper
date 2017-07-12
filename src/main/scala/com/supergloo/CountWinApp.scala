package com.supergloo

import scala.collection.mutable.ArraySeq
import webscrapper.Role
import webscrapper.Result
import scala.collection.immutable.ListMap
import webscrapper.util.StatUtils
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import com.supergloo.Util._
import webscrapper.database.DB

object CountWinApp {
  
  def winMetrics(op : Stat) = {
    op.maxWin.toDouble / Math.log(Math.sqrt(op.count))
  }
  
  def isWinner(result:Result, role:Role) = {
    result match {
      case Result.MAFIA_WIN => role == Role.MAFIA || role==Role.BOSS
      case Result.GOROD_WIN => (role != Role.MAFIA && role!=Role.BOSS)
    }
  }
  
  def groupByCount(op : (String, Stat)) = {
    op._2.count match {
      case i:Int if i > 80 && i < 200 => "80-199" 
      case i:Int if i >=200 && i < 500 => "200-499" 
      case i:Int if i >=500 && i < 1000 => "500-999" 
      case i:Int if i >=1000 && i < 2000 => "1000-1999" 
      case i:Int if i >=2000 => "> 2000" 
    }
  }
  
  def determineWinners(result:String, players:String) = {
    try{
    val res = Result.get(result)
    val arr = players.split(",").map(_.replaceAll("name =", "")).map(_.replaceAll("role =", "")).map(_.trim).map(_.replaceAll("Любит на 99,9", "Любит на 99"))
    val ev = even(arr)
    val od = odd(arr)
    val zipped = od.zip(ev).map(e => e._1 -> Role.getByRoleName(e._2)).filter(_._2 != Role.MANIAC)
    val (winners, losers) = zipped.partition(d => isWinner(res, d._2))
    val w = winners.map(_._1)
    val l = losers.map(_._1)
    w -> l
    } catch {
     case e: Exception => {
       println(players); (new ArraySeq[String](0) -> new ArraySeq[String](0) )
     }
   }
  }
  
  case class Stat(maxWin:Int=0, maxLose:Int=0, curWin:Int=0, curLose:Int=0, count:Int=0, countWin:Int = 0, percentWin:Double =0.0)
  
  def main(args:Array[String]) = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("SparkMafia")
    //.setJars(List("C:\\tmp\\hive\\spark-streaming-example-assembly-1.0.jar"))
    val sc = new SparkContext(conf)
    val map = collection.mutable.HashMap[String, Stat]()
    val games = DB().loadPlayers(2016)
    games.foreach(g => {
      val (winners, losers) = determineWinners(g._2, g._1)
      winners.foreach(w => {
        val stat = map.getOrElse(w, Stat())
        val win = stat.curWin + 1
        val maxWin = if (win > stat.maxWin) win else stat.maxWin
        val lose = 0
        val maxLose = stat.maxLose
        val count = stat.count + 1
        val countWin = stat.countWin + 1
        map.update(w, Stat(maxWin, maxLose, win, lose, count, countWin, StatUtils.roundDouble(countWin.toDouble / count * 100)))
      })
      losers.foreach(w => {
        val stat = map.getOrElse(w, Stat())
        val lose = stat.curLose + 1
        val maxLose = if (lose > stat.maxLose) lose else stat.maxLose
        val win = 0
        val maxWin = stat.maxWin
        val count = stat.count + 1
        val countWin = stat.countWin
        map.update(w, Stat(maxWin, maxLose, win, lose, count, countWin, StatUtils.roundDouble(countWin.toDouble / count * 100)))
      })
    })
    //println(ListMap(map.toSeq.sortBy(_._2.maxWin).reverse:_*))
    //println(ListMap(map.toSeq.sortBy(_._2.maxLose).reverse:_*))
    //println(ListMap(map.toSeq.sortBy(_._2.count).reverse:_*))
    //println(ListMap(map.filter(_._2.count > 10).toSeq.sortBy(e => e._2.maxWin.toDouble / Math.log(Math.sqrt(e._2.count))).reverse:_*))
    println(map.get("Aspiring"))
    //println(map.get("Miracle-man"))
    /*println(winMetrics(map.get("Желчный пузырь").get))
    println(winMetrics(map.get("Грустника").get))*/
    //zipped.foreach(println)
    /*println(ListMap(map.filter(_._2.count > 80).toSeq.sortBy(e => Math.log(Math.pow(e._2.countWin.toDouble, 2)) / Math.log(Math.pow(e._2.count, 2))).reverse:_*))
    val map3 = map.filter(_._2.count > 80).map{case (k, v) => s"${v.count.toDouble},"}
    map3.foreach(println)*/
    /*println(map.get("lost"))
    println(map.get("Желчный пузырь"))
    println(map.get("Aytsider"))
    println(map.get("KillHips"))
    println(map.get("BlueeyesKitten"))*/
    /*val map2 = ListMap(map.filter(_._2.count > 30).toSeq.sortBy(e => e._2.percentWin).reverse:_*)
    val map3 = map.filter(_._2.count > 30).groupBy(groupByCount)
    val map4 = map3.map(e => e._1 -> {
      val g = e._2.map(_._2).map(_.percentWin)
      val f = g.sum / g.size
      f
    })
    println(map4)
    map2.take(15).foreach(e => println(e._1 -> e._2.percentWin))*/
    /*val map4 = map.filter(_._2.count > 80).groupBy(groupByCount).map{case (k,v) => k -> v.map(_._2)}
    map4.foreach(d => {
      val key = d._1
      val value = d._2.toList.map(_.percentWin)
      val rdd = sc.parallelize(value, 3)
      val statCounter = rdd.stats()
      println("Key:    " + key);
       println("_________________________________________");
    println("Count:    " + statCounter.count);
    println("Min:      " + statCounter.min);
    println("Max:      " + statCounter.max);
    println("Sum:      " + statCounter.sum);
    println("Mean:     " + statCounter.mean);
    println("Variance: " + statCounter.variance);
    println("Stdev:    " + statCounter.stdev);
    println("Stdev2:    " + Math.sqrt(Math.log(statCounter.stdev*statCounter.stdev)));
    println("_________________________________________");*/
    /*val start = statCounter.mean - statCounter.stdev
    val finish = statCounter.mean + statCounter.stdev
    val filtered = rdd.filter(e => e >=start)
    val statCounter2 = filtered.stats()
      println("Key:Filtered    " + key);
       println("_________________________________________");
    println("Count:    " + statCounter2.count);
    println("Min:      " + statCounter2.min);
    println("Max:      " + statCounter2.max);
    println("Sum:      " + statCounter2.sum);
    println("Mean:     " + statCounter2.mean);
    println("Variance: " + statCounter2.variance);
    println("Stdev:    " + statCounter2.stdev);
    println("_________________________________________");*/
    //})
    
  }
}