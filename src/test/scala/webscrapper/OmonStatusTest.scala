package webscrapper

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class OmonStatusTest extends FunSuite with Matchers {
  test("omonStatus should be finded") {
    val omonStatus = OmonStatus.byCount(1)
    omonStatus should be (OmonStatus.ONE)
  }
  
  test("omonStatus should be finded as well") {
    val omonStatus = OmonStatus.byCount(OmonStatus.ONE.countMafia)
    omonStatus should be (OmonStatus.ONE)
  }
  
  test("omonStatus should throw exception in case where argument is invalid") {
    intercept[IllegalArgumentException]{
      OmonStatus.byCount(4)
    }
  }
  
}