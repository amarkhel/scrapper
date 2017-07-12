package webscrapper
import webscrapper.util.StatUtils._

case class Achievement(points:Double, description:String) {
  require(description!= null && !description.isEmpty)
  def ratingPoints:Double = roundDouble(points)
}