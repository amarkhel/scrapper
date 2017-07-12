package com.supergloo

import org.apache.spark.rdd.RDD
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import webscrapper.Role
import webscrapper.Location
import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter

object CountWordsApp {
  
  def toPair(text:String) = {
    val s = text.split(" ")
    (s(0).trim, s(1).trim.toInt)
  }
  
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[*]").setAppName("SparkMafia")
    //.setJars(List("C:\\tmp\\hive\\spark-streaming-example-assembly-1.0.jar"))
    val sc = new SparkContext(conf)
   val words = sc.textFile("counts.txt", 5)
   val mapped = words.map(_.replaceAll("\\(", ""))
   val m2 = mapped.map(_.replaceAll("\\)", ""))
   val m3 = m2.map(_.replaceAll(",", " "))
   val m4 = m3.map(toPair)
   val m5 = m4.sortBy(_._2, false, 5)
   val m6 = m5.zipWithIndex
   val m7 = m6.top(1000)
   val m8 = m7.map(_._1).sortBy(_._2).reverse.take(500).zipWithIndex
   println(s"[spoiler=Статистика 2016 самые употребляемые слова]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Слово[/th][th]Количество[/th][/tr]")
    m8.foreach(r => {
      println(s"[tr][td]${r._2 + 1}[/td][td]${r._1._1}[/td][td]${r._1._2}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
    val s= m8.map(_._1).map(_._1)
    val file = new File("words.txt")
          val bw = new BufferedWriter(new FileWriter(file))
          for (line <- s) bw.write(line + "\n")
          bw.close()
    sc.stop
  }
}