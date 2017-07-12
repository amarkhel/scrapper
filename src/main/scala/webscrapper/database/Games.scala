package webscrapper.database

import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global

class Games(tag: Tag) extends Table[(Int, Int, Int, String, String, Int, String, String, Int, Int, Int)](tag, "games") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc) // This is the primary key column
  def gameId = column[Int]("gameId")
  def playersSize = column[Int]("playersSize")
  def players = column[String]("players")
  def location = column[String]("location")
  def result = column[String]("result")
  def tournamentResult = column[String]("tournamentResult")
  def rounds = column[Int]("rounds")
  def year = column[Int]("year")
  def month = column[Int]("month")
  def day = column[Int]("day")

  def * = (id, gameId, playersSize, players, location, rounds, result, tournamentResult, year, month, day)
}