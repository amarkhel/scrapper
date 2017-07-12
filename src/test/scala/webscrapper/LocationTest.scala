package webscrapper

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class LocationTest extends FunSuite with Matchers {
  test("location should be finded") {
    val location = Location.get("Улица Крещения")
    location should be (Location.KRESTY)
  }
  
  test("location should be finded as well") {
    val location = Location.get(Location.KRESTY.name)
    location should be (Location.KRESTY)
  }
  
  test("location should throw exception in case where there no statuses") {
    intercept[IllegalArgumentException]{
      Location.get("Not Exist")
    }
  }
  
  test("location should throw exception in case of null argument") {
    intercept[IllegalArgumentException]{
      Location.get(null)
    }
  }
}