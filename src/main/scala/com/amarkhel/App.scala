package com.amarkhel

import webscrapper.Properties
import webscrapper.service.GameService
import scala.io.Source
import java.nio.file.Paths
import java.nio.file.Files
import java.nio.charset.StandardCharsets
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.MongoClient
import org.mongodb.scala.MongoCollection
import java.util.concurrent.TimeUnit

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import org.mongodb.scala.model.Filters._

import org.mongodb.scala._
import com.amarkhel.Helpers._
import com.thoughtworks.xstream.XStream
import org.mongodb.scala.bson.BsonArray
import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter

/**
 * @author ${user.name}
 */
object App {

  def main(args : Array[String]) {

    val regex = """\[ОМОНОВЕЦ\]  <b style=\"text-decoration: underline; color: cyan;\">Маньяк (.*?) убивает (.+).</b>""".r
    val str = "[ОМОНОВЕЦ]  <b style=\"text-decoration: underline; color: cyan;\">Маньяк мафия убивает убивает N_a_s_t_y_a.</b>"
    str match {
      case regex(a1, a2) => {
        println(a1 + a2)
      }
      case _=> println("Fuck")
    }
    //2720229,2815241,3694168
    val s = new GameService
    val g = s.loadGame("2400270", true)
    println(g)
    /*val service = new GameService
    val parts = FileParser.parseParts(Properties.path + "partii.txt")
    parts.map {_.games }.flatten.foreach(id => {
      val html = Source.fromURL(s"http://mafiaonline.ru/log/$id").mkString
      val file = Paths.get(Properties.path + id + ".html")
      if(!Files.exists(file)) Files.write(file, html.getBytes(StandardCharsets.UTF_8))
    })
    val t = service.createTournamentFake("Test")
    t._messages*/
    /*val mongoClient: MongoClient = MongoClient()

    val database: MongoDatabase = mongoClient.getDatabase("test");
    val collection: MongoCollection[Document] = database.getCollection("TournamentEntity")
    val docs = collection.find().first().results()
    println(docs)*/ 
    /*val service = new GameService
    val game = service.loadGame("3682164", None)
    println(game.id)*/
    /*val service = new GameService
    service.cleanup("Семейный кубок 2016")
    println(service.findAllTournaments)
    
    val t = service.createTournament("Межклан 2016")
    println("Citizen" + t.countCitizenWords)
    println("Mafia" + t.countMafiaWords)
    println("Komissar" + t.countKomissarWords)
    println("Citizen" + t.countCitizenWords.size)
    println("Mafia" + t.countMafiaWords.size)
    println("Komissar" + t.countKomissarWords.size)
    t.wordsMap.map{case (key,map) => {
      val file = new File(key + ".txt")
      //println(file.name)
      val bw = new BufferedWriter(new FileWriter(file))
      val content = map.map{case (k,v) => s"$k : $v"}.toList.mkString("\n")
      bw.write(content)
      bw.close()
    }}*/
    //println(Source.fromFile("C:\\Users\\amarkhel\\Documents\\d.csv", "UTF-8").getLines.mkString(","))
    /*val s = new GameService
    val a = s.createAnalyzer*/
    /*println("Количество ночных раундов которые пережил маньяк " + a.maniacAliveAverage)
    println("Среднее количество трупов от маньяка " + a.maniacCountKillsAverage)
    println("Вероятности исходов в зависимости от того сколько прожил маньяк " + a.maniacWinProbability)
    println("Первый труп маньяка " + a.maniacFirstKillDistribution)*/
    /*println("Статистика исходов " + a.mapResultsPercent)
    println("Статистика исходов с посаженным ребенком " + a.mapResultsChildsPrisonedFirst)*/
    /*println("Количество ночных раундов которые пережил доктор " + a.doctorAliveAverage)
    println("Среднее количество вылеченных от доктора " + a.doctorCountSavesAverage)
    println("Вероятности исходов в зависимости от того сколько прожил доктор " + a.doctorWinProbability)
    println("Первый вылеченный " + a.doctorFirstSaveDistribution)
    println("Первый кого лечил " + a.doctorFirstAttemptDistribution)*/
    /*println("Количество ночных раундов которые пережил комиссар " + a.komissarAliveAverage)
    println("Количество ночных раундов которые пережил сержант " + a.serzhantAliveAverage)
    println("Среднее количество проверенных от комиссара и сержанта " + a.komissarCountChecksAverage)
    println("Вероятности исходов в зависимости от того сколько прожил комиссар " + a.komissarWinProbability)
    println("Вероятности исходов в зависимости от того сколько прожил сержант " + a.serzhantWinProbability)
    println("Первый проверенный " + a.komissarFirstCheckDistribution)
    println("Количество ночных раундов которые пережил босс " + a.bossAliveAverage)
    println("Среднее количество морозов " + a.bossCountFreezeAverage)
    println("Вероятности исходов в зависимости от того сколько прожил босс " + a.bossWinProbability)
    println("Первый мороженный " + a.bossFirstFreezeDistribution)*/
    /*val g = DAO.load(3517073).left.get.get
    val pl = g.statistics.playersInRole(_.isManiac)(0)
    println(g.statistics.maniacDuplets(pl))*/
  }

}

