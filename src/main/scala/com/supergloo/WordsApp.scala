package com.supergloo

import webscrapper.database.DB
import webscrapper.Location
import org.apache.spark.rdd.RDD
import org.apache.spark.ml.feature.StopWordsRemover
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.ml.feature.IDF
import org.apache.spark.ml.feature.HashingTF
import webscrapper.util.RussianStemmer
import scala.collection.immutable.ListMap

object WordsApp {
  
  val cache = new collection.mutable.HashMap[String, Set[String]]
  
  def messagesFrom(name:String) = {
    val cached = cache.get(name)
    if (cached.isDefined) cached.get else {
      val games = DB().loadGames(2016, Location.SUMRAK, name, 7)
      println("games are loaded for " + name)
      val messages = games.filter(_.findPlayer(name).isDefined).flatMap(g => {
      val player = g.findPlayer(name).get
      val filtered = g.statistics.messageStats.messages.filter(_.from(player)).map(_.normalise()).flatMap(_.split(" "))
      filtered
    })
    println("messages are ready for " + name)
    val mesMap = messages.groupBy(identity).map(f => f._1 -> f._2.size).filter(_._2 > 5).filter(!RussianStemmer.stopWords.contains(_))
    val top = ListMap(mesMap.toSeq.sortWith(_._2 > _._2):_*).take(1000)
    println("messages are filtered for " + name)
    top.foreach(println)
    val res = top.map(_._1).toSet
    res.foreach(println)
    println(res.size)
    cache.put(name, res)
    res
    }
    
  }
  
  def compare(name1:String, name2:String) = {
    val messages = messagesFrom(name1)
    val messages2 = messagesFrom(name2)
    println(s"comparing $name1 and $name2")
    val jSimilarity = messages.intersect(messages2).size/(messages.union(messages2).size.toDouble)
    println(s"Похожесть $name1 и $name2" + jSimilarity)
  }
  def main(args: Array[String]) = {

    compare("весельчакджо", "Ventex")
    compare("весельчакджо", "Желчный пузырь")
    compare("Желчный пузырь", "Ventex")
    compare("Miss May", "Симба")
    compare("3лОй кОт", "lost")
    compare("3лОй кОт", "весельчакджо")
    compare("3лОй кОт", "Желчный пузырь")
    compare("3лОй кОт", "Симба")
    
  }
}