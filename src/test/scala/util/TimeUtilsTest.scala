package webscrapper.util

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import webscrapper.TestUtils._
import java.time.LocalDateTime

@RunWith(classOf[JUnitRunner])
class TimeUtilsTest extends FunSuite with Matchers {

  test("for format method"){
    val time = LocalDateTime.of(2017, 1, 1, 20, 20)
    
    val result = TimeUtils.format(time)
    
    result should be ("2017-01-01 20:20:00 по московскому времени")
/*    
    intercept[IllegalArgumentException]{
      TimeUtils.format(null)
    }*/
  }
  
  test("for formatDuration method"){
    var start = LocalDateTime.of(2017, 1, 1, 20, 20, 0)
    var finish = LocalDateTime.of(2017, 1, 1, 20, 20, 30)
    val result = TimeUtils.formatDuration(start, finish)
    
    result should be (" 0 минут 30 секунд")
    
    intercept[IllegalArgumentException]{
      TimeUtils.formatDuration(null, null)
    }
    intercept[IllegalArgumentException]{
      TimeUtils.formatDuration(null, start)
    }
    intercept[IllegalArgumentException]{
      TimeUtils.formatDuration(start, null)
    }
    intercept[IllegalArgumentException]{
      TimeUtils.formatDuration(finish, start)
    }
  }
  
  test("for toMinAndSec method"){
    var start = LocalDateTime.of(2017, 1, 1, 20, 20, 0)
    var finish = LocalDateTime.of(2017, 1, 1, 20, 20, 30)
    TimeUtils.toMinAndSec(start, finish) should be ((0, 30))
    finish = LocalDateTime.of(2017, 1, 1, 20, 21, 30)
    TimeUtils.toMinAndSec(start, finish) should be ((1, 30))
    start = LocalDateTime.of(2017, 1, 1, 20, 20, 49)
    TimeUtils.toMinAndSec(start, finish) should be ((0, 41))
    intercept[IllegalArgumentException]{
      TimeUtils.toMinAndSec(null, null)
    }
    intercept[IllegalArgumentException]{
      TimeUtils.toMinAndSec(null, finish)
    }
    intercept[IllegalArgumentException]{
      TimeUtils.toMinAndSec(start, null)
    }
    intercept[IllegalArgumentException]{
      TimeUtils.toMinAndSec(finish, start)
    }
    start = LocalDateTime.of(2017, 1, 1, 20, 59, 49)
    finish = LocalDateTime.of(2017, 1, 1, 21, 0, 30)
    TimeUtils.toMinAndSec(start, finish) should be ((0, 41))
    finish = LocalDateTime.of(2017, 1, 1, 21, 12, 30)
    TimeUtils.toMinAndSec(start, finish) should be ((12, 41))
    finish = LocalDateTime.of(2017, 1, 1, 22, 12, 30)
    TimeUtils.toMinAndSec(start, finish) should be ((72, 41))
  }
  
}