package webscrapper.util
import webscrapper.persistence.PenaltyEntity

import scala.io.Source
import java.io.{ FileReader, FileNotFoundException, IOException }
import webscrapper.persistence.TeamEntity
import webscrapper.persistence.PlayerEntity
import webscrapper.persistence.PartEntity
import webscrapper.Team
import webscrapper.User
import webscrapper.Penalty

object FileParser {

  private def read[T](file: String, maker: Array[String] => T): List[T] = {
    require(file != null && !file.isEmpty)
    val entities = for (line <- Source.fromFile(file, "UTF-8").getLines.filter(!_.isEmpty)) yield maker(line.split(":"))
    entities.toList
  }

  def parseTeams(file: String) = {
    read(file, (line: Array[String]) => {
      val team = line(0).trim
      val players = line(1).split(",").map {_.trim}
      new Team(team, players.map(new User(_)).toList)
    })
  }

  def parseParts(file: String): List[PartEntity] = {
    read(file, (line: Array[String]) => {
      val name = line(0).trim
      val games = line(1).split(",")
      new PartEntity(name, games.map(_.trim.toLong).toList)
    })
  }

  def parsePenalties(file: String, teams: List[Team]): List[Penalty] = {
    require(teams != null && teams.size > 0)
    read(file, (line: Array[String]) => {
      val gameId = line(0).trim.toLong
      val teamName = line(1).trim
      val player = line(2).trim
      val descr = line(3).trim
      val points = line(4).trim.toDouble
      val team = teams.filter(_.name == teamName)(0)
      new Penalty(gameId, descr, team.players.find(_.name == player).getOrElse(null), team, points)
    })
  }
}