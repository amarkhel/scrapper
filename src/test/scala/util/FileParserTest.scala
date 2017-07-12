package webscrapper.util

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import webscrapper.TestUtils._
import java.io.FileNotFoundException

@RunWith(classOf[JUnitRunner])
class FileParserTest extends FunSuite with Matchers {

  test("parse teams should work correctly"){
    val result = FileParser.parseTeams("src/main/resources/sk2016.txt")
    result.size should be (16)
    result(0).name should be ("Упийцы")
    result(0).players.size should be (8)
    result(0).players(0).name should be ("apaapaapa")
    result(15).name should be ("Боги мафии")
    result(15).players.size should be (5)
    result(15).players(0).name should be ("Мальок")
    
    intercept[IllegalArgumentException]{
      FileParser.parseTeams(null)
    }
    intercept[FileNotFoundException]{
      FileParser.parseTeams("Not exist")
    }
    intercept[IllegalArgumentException]{
      FileParser.parseTeams("")
    }
  }
  
  test("methos parsePenalties should work correctly"){
    val teams = FileParser.parseTeams("src/main/resources/sk2016.txt")
    intercept[IllegalArgumentException]{
      FileParser.parsePenalties(null, teams)
    }
    intercept[FileNotFoundException]{
      FileParser.parsePenalties("Not exist", teams)
    }
    intercept[IllegalArgumentException]{
      FileParser.parsePenalties("", teams)
    }
    intercept[IllegalArgumentException]{
      FileParser.parsePenalties("src/main/resources/sk_penalties.txt", null)
    }
    val penalties = FileParser.parsePenalties("src/main/resources/sk_penalties.txt", teams)
    penalties.size should be (12)
    penalties(0).gameId should be (3624161)
    penalties(0).descr should be ("штраф")
    penalties(0).points should be (0.8)
    penalties(0).player.name should be ("fedanet")
    penalties(0).team.name should be ("Vendetta")
    penalties(5).gameId should be (3627767)
    penalties(5).descr should be ("аннулироварие за партию (нарушение регламента)")
    penalties(5).points should be (0.1)
    penalties(5).player.name should be ("Хоккинг")
    penalties(5).team.name should be ("Лига Выдающихся Мафиози")
  }
  
  test("parse parts should work correctly"){
    val result = FileParser.parseParts("src/main/resources/partii.txt")
    result.size should be (3)
    result(0).name should be ("Этап1")
    result(0).games.size should be (15)
    result(0).games(0) should be (3625830)
    result(0).games(14) should be (3624505)
    result(2).games(14) should be (3629887)
    intercept[IllegalArgumentException]{
      FileParser.parseTeams(null)
    }
    intercept[FileNotFoundException]{
      FileParser.parseTeams("Not exist")
    }
    intercept[IllegalArgumentException]{
      FileParser.parseTeams("")
    }
  }
}