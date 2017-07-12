package com.supergloo

import webscrapper.database.DB
import webscrapper.Location
import scala.collection.mutable.ListBuffer
import webscrapper.TournamentResult
import webscrapper.Role
import webscrapper.Result.GOROD_WIN

object VeselApp2 {

  def getPoints(result: TournamentResult) = {
    result match {
        case TournamentResult.GOROD_WIN => 1.0
        case TournamentResult.OMON_4    => 0.25
        case TournamentResult.OMON_3    => 0.25
        case TournamentResult.OMON_2    => 0.5
        case TournamentResult.OMON_1    => 0.75
        case TournamentResult.MAFIA_WIN => 0.0
    }
  }

  def getPersonal(name: String) = {
    val map = new collection.mutable.HashMap[Int, ListBuffer[Double]]
    val map2 = new collection.mutable.HashMap[Int, ListBuffer[Double]]
    DB().loadGames(2016, Location.SUMRAK, name, 6).foreach(g => {
      val pl = g.findPlayer(name)
      
      if (pl.isDefined && pl.get.isCitizen) {
        val stat = g.statistics.aliveTill(name)
        val index = stat._1
        
        val points = getPoints(g.tournamentResult)
          val list = map.getOrElse(index, new ListBuffer[Double])
          list += points
          map.update(index, list)
      }
    })
    for (i <- 1 to map.size){
      val before = map.filter(_._1 < i).flatMap(_._2)
      val percentBefore = before.sum/ before.size
      val after = map.filter(_._1 >= i).flatMap(_._2)
      val percentAfter = after.sum/ after.size
      println(name)
      println(s"Последний посадочный ход ${i} - $percentBefore, после - $percentAfter")
    }
    /*val grouped = pl.groupBy(_._1)
    case class Stat(name:String, count:Int = 0, countPris:Int =0, kef:Double =0.0, percent:String = "")
    val playersMap = new ListBuffer[Stat]()
    grouped.foreach(g => {
      val name = g._1
      val games = g._2.map(_._2)
      playersMap += new Stat(name, games.size, games.sum, games.sum/games.size, games.sum/games.size * 100 + "%")
    })
    val rating = playersMap.filter(_.count > 50).sortBy(_.kef).reverse.zipWithIndex
    println(s"[spoiler=Рейтинг первых посадок]")

   println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Ник[/th][th]Число игр[/th][th]Число посадок[/th][th]Процент посадок[/th][/tr]")
    rating.foreach(res => {
      val rate = res._1
      println(s"[tr][td]${res._2 + 1}[/td][td][nick]${rate.name}[/nick][/td][td]${rate.count}[/td][td]${rate.countPris}[/td][td]${rate.kef}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")*/
  }

  def main(args: Array[String]) = {
    /*val name1 = "olsen"
    val name2 = "Хоккинг"
    val name1 = "Unstoppable"
    val name2 = "Антон Городецкий"
    val a1 = calculatePersonal(name1)
    val a2 = calculatePersonal(name2)
    println(s"[spoiler=$name1 vs $name2 Ожиданка+Кресты]")

   println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Ник[/th][th]Процент побед команды, если сел первым[/th][th]Процент побед команды, если НЕ сел первым[/th][th]Число игр[/th][th]Посадки первым ходом[/th][/tr]")
    println(s"[tr][td][nick]${name1}[/nick][/td][td]${a1._1._2}[/td][td]${a1._2._2}[/td][td]${a1._1._4}[/td][td]${a1._1._3}[/td][/tr]")
    println(s"[tr][td][nick]${name2}[/nick][/td][td]${a2._1._2}[/td][td]${a2._2._2}[/td][td]${a2._1._4}[/td][td]${a2._1._3}[/td][/tr]")
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
    //calculatePersonal("Картошка")
*/  
    /*getPersonal("весельчакджо")
    getPersonal("Картошка")
    getPersonal("prizrock")
    getPersonal("KillHips")
    getPersonal("rimus")
    getPersonal("Вилли Токарев")
    getPersonal("Mark")
    getPersonal("Flur")
    getPersonal("Vera")
    getPersonal("Envy")
    getPersonal("Веро")
    getPersonal("Unstoppable")
    getPersonal("Намико")
    getPersonal("injected")
    getPersonal("Желтый Майк")
    getPersonal("Lion")
    getPersonal("Kallocain")
    getPersonal("BlueeyesKitten")
    getPersonal("фей хуа")
    getPersonal("A La Magnifique")
    getPersonal("3лОй кОт")
    getPersonal("lost")
    getPersonal("Bazinga")
    getPersonal("Jerry Tough")*/
    getPersonal("Aspiring")
    }
}