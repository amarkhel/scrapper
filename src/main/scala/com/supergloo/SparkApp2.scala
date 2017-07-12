package com.supergloo

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import scala.collection.mutable.ListBuffer
import org.apache.spark.SparkContext
import scala.collection.mutable.HashMap
import reflect.runtime.universe._
import org.apache.spark.rdd.RDD
import com.supergloo.Game

/**
 * Spark Streaming Example App
 */
object SparkApp2 {

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local[*]").setAppName("SparkMafia")
    val sc = new SparkContext(conf)
    val lines = sc.textFile("games_from_db.txt", 20)
    val zero = new ListBuffer[Int]()
    def makeLeaf(games: Iterable[Int]) = {
      val count = games.size;
      val maxRounds = games.max;
      val minRounds = games.min;
      val average = games.sum.toDouble / games.size;
      Stat(count, minRounds, maxRounds, average)
    }

    def unwrap(name: String, map: Any): NamedStat = {
      map match {
        case m2: Map[_, _] => {
          val childs = m2.map { case (k, v) => { unwrap(k.toString, v) } }.toList.sortBy(_.name)

          val stats = childs.map(_.stat)
          val count = stats.map(_.count).sum
          val maxRounds = stats.map(_.maxRounds).max
          val minRounds = stats.map(_.minRounds).min
          val average = stats.map(_.averageRounds).sum / childs.size
          val stat = Stat(count, minRounds, maxRounds, average)
          NamedStat(name, stat, childs)
        }
        case t: Stat => NamedStat(name, t, Nil)
      }
    }
    def roundDouble(arg:Double):Double = BigDecimal(arg).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
    
    def print(stat: NamedStat, count:Int): ListBuffer[String] = {
      val ret = stat.toString 
      val addon = if (count > 0) s" Процент от всего количества игр: ${roundDouble(stat.count.toDouble/count*100)}% " else ""
      val result = ret + addon
      if(stat.children.isEmpty) ListBuffer("----" + result) else {
        val count = stat.count
        val childs = stat.children.toList.flatMap(x => print(x, count)).map(x => "\t" + x)
        val toPrint = ListBuffer("----" + result) ++ childs
        toPrint
      }
    }
    
    val games = for (
      line <- lines;
      val sp = line.split(";");
      val game = Game(sp(0).toInt, sp(1).toInt, sp(2), sp(3), sp(4).toInt, sp(5), sp(6), sp(7).toInt, sp(8).toInt, sp(9).toInt)
    ) yield game
    games.cache()
    val years = games.map(game => (game.year, game.location, game.countPlayers.toString, game.tournamentResult) -> game.rounds)
      .aggregateByKey(zero, 6)(
        (set, v) => set += v,
        (set1, set2) => set1 ++= set2)
    
    val years2 = years.mapValues(makeLeaf).collectAsMap
    val years3 = years2.groupBy(_._1._1).mapValues(_.map(x => ((x._1._2, x._1._3, x._1._4), x._2)))
    //val games4 = games3.groupBy(_._2zzz).mapValues( _.map( x => (( x._1._2, x._1._3), x._2)))
    val years4 = years3.mapValues(_.groupBy(_._1._1).mapValues(_.map(x => ((x._1._2, x._1._3), x._2))))
    val years5 = years4.mapValues(_.mapValues(_.groupBy(_._1._1).mapValues(_.map(x => ((x._1._2), x._2)))))
    //games5.foreach(println)
    val years6 = unwrap("Overall", years5)
    val years7 = print(years6, 0)
    
    val countPlayers = games.map(game => ( game.countPlayers.toString, game.location, game.tournamentResult) -> game.rounds)
      .aggregateByKey(zero, 6)(
        (set, v) => set += v,
        (set1, set2) => set1 ++= set2)
    
    val countPlayers2 = countPlayers.mapValues(makeLeaf).collectAsMap
    val countPlayers3 = countPlayers2.groupBy(_._1._1).mapValues(_.map(x => ((x._1._2, x._1._3), x._2)))
    val countPlayers4 = countPlayers3.mapValues(_.groupBy(_._1._1).mapValues(_.map(x => ((x._1._2), x._2))))
    val countPlayers6 = unwrap("Overall", countPlayers4)
    val countPlayers7 = print(countPlayers6, 0)
    //countPlayers7.foreach(println)
    
    val count = games.map(game => ( game.countPlayers.toString, game.tournamentResult) -> game.rounds)
      .aggregateByKey(zero, 6)(
        (set, v) => set += v,
        (set1, set2) => set1 ++= set2)
    
    val count2 = count.mapValues(makeLeaf).collectAsMap
    val count3 = count2.groupBy(_._1._1).mapValues(_.map(x => ((x._1._2), x._2)))
    val count6 = unwrap("Overall", count3)
    val count7 = print(count6, 0)
    count7.foreach(println)
    
    
    sc.stop
  }

}