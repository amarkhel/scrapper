package webscrapper.database

import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global

class InvalidGames(tag: Tag) extends Table[(Int, Int, String)](tag, "invalid") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc) // This is the primary key column
  def gameId = column[Int]("gameId")
  def reason = column[String]("reason")

  def * = (id, gameId, reason)
}