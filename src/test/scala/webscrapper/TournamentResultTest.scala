package webscrapper

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TournamentResultTest extends FunSuite with Matchers {
  
  test("result should be finded") {
    val result = TournamentResult.byDescription("Победа города")
    result should be (TournamentResult.GOROD_WIN)
  }
  
  test("result should be finded as well") {
    val result = TournamentResult.byDescription(TournamentResult.GOROD_WIN.descr)
    result should be (TournamentResult.GOROD_WIN)
  }
  
  test("result should throw exception in case where there no result") {
    intercept[IllegalArgumentException]{
      TournamentResult.byDescription("Not Exist")
    }
  }
  
  test("resultshould throw exception in case of null argument") {
    intercept[IllegalArgumentException]{
      TournamentResult.byDescription(null)
    }
  }
}