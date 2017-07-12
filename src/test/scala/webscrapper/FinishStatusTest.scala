package webscrapper

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class FinishStatusTest extends FunSuite with Matchers {
  
  test("finishStatus should be finded") {
    val status = FinishStatus.get("Убит")
    status should be (FinishStatus.KILLED)
  }
  
  test("finishStatus should be finded as well") {
    val status = FinishStatus.get(FinishStatus.KILLED.status)
    status should be (FinishStatus.KILLED)
  }
  
  test("finishStatus should throw exception in case where there no statuses") {
    intercept[IllegalArgumentException]{
      FinishStatus.get("Not Exist")
    }
  }
  
  test("finish status should throw exception in case of null argument") {
    intercept[IllegalArgumentException]{
      FinishStatus.get(null)
    }
  }
}