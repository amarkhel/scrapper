package webscrapper 

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AchievementTest extends FunSuite with Matchers {
  
  test("achievement should return properly rounded points") {
    val achievement = Achievement(2.333, "descr")
    achievement.ratingPoints should be (2.33) 
  }
  
  test("achievement should return properly untouched points") {
    val achievement = Achievement(1.0, "descr")
    achievement.ratingPoints should be (1.0) 
  }
}