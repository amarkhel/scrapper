package webscrapper

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RoleTest extends FunSuite with Matchers {
  
  test("role should be finded") {
    val role = Role.get("Босс")
    role should be (Role.BOSS)
  }
  
  test("role should be finded as well") {
    val role = Role.get(Role.BOSS.role)
    role should be (Role.BOSS)
  }
  
  test("role should throw exception in case where there no roles") {
    intercept[IllegalArgumentException]{
      Role.get("Not Exist")
    }
  }
  
  test("role should throw exception in case of null argument") {
    intercept[IllegalArgumentException]{
      Role.get(null)
    }
  }
  
 /* test("role is mafia should be determined correctly") {
    Role.BOSS.isMafia should be (true)
    Role.BOSS.isRole should be (true)
    Role.BOSS.isImportantGoodRole should be (false)
    Role.KOMISSAR.isMafia should be (false)
    Role.KOMISSAR.isRole should be (true)
    Role.KOMISSAR.isImportantGoodRole should be (true)
    Role.MAFIA.isMafia should be (true)
    Role.MAFIA.isRole should be (true)
    Role.MAFIA.isImportantGoodRole should be (false)
    Role.MANIAC.isMafia should be (false)
    Role.MANIAC.isRole should be (true)
    Role.MANIAC.isImportantGoodRole should be (false)
    Role.SERZHANT.isMafia should be (false)
    Role.SERZHANT.isRole should be (true)
    Role.SERZHANT.isImportantGoodRole should be (true)
    Role.DOCTOR.isMafia should be (false)
    Role.DOCTOR.isRole should be (true)
    Role.DOCTOR.isImportantGoodRole should be (true)
    Role.CHILD.isMafia should be (false)
    Role.CHILD.isRole should be (true)
    Role.CHILD.isImportantGoodRole should be (false)
    Role.CHILD_GIRL.isMafia should be (false)
    Role.CHILD_GIRL.isRole should be (true)
    Role.CHILD_GIRL.isImportantGoodRole should be (false)
    Role.CITIZEN.isMafia should be (false)
    Role.CITIZEN.isRole should be (false)
    Role.CITIZEN.isImportantGoodRole should be (false)
  }*/
}