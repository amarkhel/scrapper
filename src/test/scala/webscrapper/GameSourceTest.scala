package webscrapper

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GameSourceTest extends FunSuite with Matchers {
  test("should throw exception when file not exist") {
    intercept[IllegalArgumentException]{
      GameSource.fromResource("Not Exist")
    }
    intercept[IllegalArgumentException]{
      GameSource.fromFile(null)
    }
  }
  
  test("should throw exception when URL not exist") {
    intercept[IllegalArgumentException]{
      GameSource.fromUrl("Not Exist")
    }
    intercept[IllegalArgumentException]{
      GameSource.fromUrl(null)
    }
  }
  
  test("should success when URL exist") {
    val document = GameSource.fromUrl("http://mafiaonline.ru/log/3669641").get
    document shouldNot be (null)
    document.childNodes.size shouldNot be (null)
  }
  
  test("should success when file exist") {
    val document = GameSource.fromResource("3623724.html").get
    document shouldNot be (null)
    document.childNodes.size shouldNot be (null)
  }
}