package webscrapper

import webscrapper.service.GameService
import scala.util.matching.Regex

object TestApp extends App {
  //def main(args:Array[String]) = {
     /*val reg = "[ОМОНОВЕЦ]  <b style=\"text-decoration: underline; color: #d5c32d;\">Комиссар (.+) проверил жителя (.+)\\.</b>".r
     val str = "[ОМОНОВЕЦ]  <b style=\"text-decoration: underline; color: #d5c32d;\">Комиссар text проверил жителя Victoire.</b>"*/
     /*val reg = """\[ОМОНОВЕЦ\] <b>(.+) (?:отправлен в тюрьму|отправлена в тюрьму|отправлен\(а\) в тюрьму).</b>""".r
     val str = "[ОМОНОВЕЦ] <b>fate отправлен(а) в тюрьму.</b>"
     applyRegex(str, reg)
     
     println(reg.findAllMatchIn(str).mkString(","))
     
     protected def applyRegex(text: String, regex: Regex) = {
    text match {
      case regex(player1) => println(player1)
    }
  }
  //}
 val service = new GameService
 val game = service.simulate(3746695, 5).get
 println(game.id)
 game.chat.foreach(println)*/
  /*val games = DAO.searchGames(Location.OZHA, List[TournamentResult](TournamentResult.MAFIA_WIN), List[Role](Role.BOSS), "apaapaapa", List[String]("Желчный пузырь"), 0, 100, 0, 100, 2015, 2017, 1, 12, 1, 31)
  games.foreach(println)*/
  val service = new GameService
 val game = service.loadGame("2275974").get
 println(game.id)
}