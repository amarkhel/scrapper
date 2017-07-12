package webscrapper

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PropertiesTest extends FunSuite with Matchers {

  /*test("path should be determined correctly"){
    System.setProperty("os.name", "Windows ")
    Properties.path should be (Properties.WINDOWS_PATH)
    System.setProperty("os.name", "Linus")
    Properties.path should be (Properties.LINUX_PATH)
  }*/
}