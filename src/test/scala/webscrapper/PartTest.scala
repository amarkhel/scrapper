package webscrapper

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import scala.collection.mutable.ListBuffer
import TestUtils._

@RunWith(classOf[JUnitRunner])
class PartTest extends FunSuite with Matchers {
  
  test("result distribution should be correct ") {
    val games = ListBuffer[Game]()
    games += makeFinishedGame(Result.GOROD_WIN)
    games += makeFinishedGame(Result.MAFIA_WIN)
    games += makeFinishedGame(Result.GOROD_WIN)
    games += makeFinishedGame(Result.GOROD_WIN, OmonStatus.ONE)
    games += makeFinishedGame(Result.GOROD_WIN)
    games += makeFinishedGame(Result.MAFIA_WIN)
    games += makeFinishedGame(Result.GOROD_WIN, OmonStatus.TWO)
    games += makeFinishedGame(Result.GOROD_WIN)
    games += makeFinishedGame(Result.GOROD_WIN)
    games += makeFinishedGame(Result.MAFIA_WIN)
    val part = new Part("first Round", games.toList, "")
    println(part.resultDistribution)
  }
}