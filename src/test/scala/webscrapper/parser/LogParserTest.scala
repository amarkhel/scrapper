package webscrapper.parser

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers
import org.scalatest.FunSuite
import webscrapper.service.GameService

@RunWith(classOf[JUnitRunner])
class LogParserTest extends FunSuite with Matchers {
  test("log parser shpould parse those games without exceptions"){
    val list = List(3746301,3766199,3770955,2275974,2295008,2299165, 2720229,2815241,3694168,3787711,3789678, 3792003, 2727416)
    val service = new GameService
    list.foreach(id => service.loadGame(id.toString, true))
  }
}