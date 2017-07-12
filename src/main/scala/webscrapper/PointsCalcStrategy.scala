package webscrapper

import java.io.IOException
import webscrapper.rules.RuleEngine
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import webscrapper._
import webscrapper.util.Logger

class PointsCalcStrategy extends Logger{
  this: RuleEngine =>
  def calculate(game: Game) = {
    Try(calculatePoints(game)) match {
      case Success(value) => value
      case Failure(e) => {
        log(e.toString)
        throw new IllegalArgumentException("Problems while calculating points")
      }
    }
  }
}