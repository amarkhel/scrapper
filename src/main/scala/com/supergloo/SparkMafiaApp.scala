//package com.supergloo
// 
//import org.apache.spark.SparkConf
//import org.apache.spark.streaming.{Seconds, StreamingContext}
//import scala.collection.mutable.ListBuffer
//import org.apache.spark.SparkContext
//import webscrapper.database.DB
//import webscrapper.Role
//import org.apache.spark.rdd.RDD
//import org.apache.spark.mllib.stat.{MultivariateStatisticalSummary, Statistics}
//import webscrapper.util.RussianStemmer
// 
///**
//  * Spark Streaming Example App
//  */
//object SparkMafiaApp {
// 
//  def toPercent(rec:(String, (Int, Int))) = {
//      val values = rec._2
//      val percent = values match {
//        case (a,b) if b > 0 => b.toDouble / a
//        case _ => 0.0
//      }
//      val percentString = (percent * 100).toString + "%"
//      (rec._1, (rec._2._1, rec._2._2, percent, percentString))
//    }
//  
//  def calculateCounts(messages:RDD[String]) = {
//      messages.map(word => (word, 1)).reduceByKey{case (x, y) => x + y}.sortBy(_._2, false, 20)
//    }
//  
//  def main(args: Array[String]) {
//    val conf = new SparkConf().setMaster("local[*]").setAppName("SparkMafia")
//    //.setJars(List("C:\\tmp\\hive\\spark-streaming-example-assembly-1.0.jar"))
//    val sc = new SparkContext(conf)
//    val games = DB().loadGames(2016)
//    val messages = sc.parallelize(games, 20)
//    messages.cache()
//    messages.saveAsTextFile("messages.txt")
//    val transformedMessages = messages.map(_._2)
//    //.filter(RussianStemmer.isOk)
//    val mafiaMessages = messages.filter(m => m._1 == Role.MAFIA || m._1 == Role.BOSS).map(_._2)
//    
//    val mafiaCounts = calculateCounts(mafiaMessages)
//    val counts = calculateCounts(transformedMessages)
//    //counts.saveAsTextFile("counts-stem.txt")
//    val union = counts.join(mafiaCounts).filter(e => e._2._1 > 10 && e._2._2 > 10).sortBy(_._2._2, false, 20)
//    val percent = union.map(toPercent).sortBy(_._2._3, false, 20)
//    val statCounter = percent.map(_._2._3).stats()
//    println("Count:    " + statCounter.count);
//    println("Min:      " + statCounter.min);
//    println("Max:      " + statCounter.max);
//    println("Sum:      " + statCounter.sum);
//    println("Mean:     " + statCounter.mean);
//    println("Variance: " + statCounter.variance);
//    println("Stdev:    " + statCounter.stdev);
//    val d = statCounter.mean + statCounter.stdev * 3
//    val mafiaWords = percent.filter(_._2._3 > d)
//    val mafiaRareWords = percent.filter(e => e._2._3 < 0.05 && e._2._1 > 50)
//    mafiaWords.saveAsTextFile("mafiaWords.txt")
//    mafiaRareWords.saveAsTextFile("mafiaRareWords.txt")
//    /*val seriesX: RDD[Double] = union.map(_._2._1)
//    val seriesY: RDD[Double] = union.map(_._2._2)
//
//    val correlation: Double = Statistics.corr(seriesX, seriesY, "pearson")
//    println(s"Correlation is: $correlation")
//
//    val correlation2: Double = Statistics.corr(seriesX, seriesY, "spearman")
//    println(s"Correlation is: $correlation2")
//
//    val testResult = Statistics.kolmogorovSmirnovTest(seriesX, "norm", 0, 1)
//    val testResult2 = Statistics.kolmogorovSmirnovTest(seriesY, "norm", 0, 1)
//    println(testResult)
//    println(testResult2)*/
//    sc.stop
//    /*filter(m => m._1 == Role.MAFIA || m._1 == Role.BOSS)*/
//  }
// 
//}