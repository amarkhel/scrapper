package webscrapper

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import TestUtils._

@RunWith(classOf[JUnitRunner])
class PlayerTest extends FunSuite with Matchers {
  test("roleName method"){
    val player = makePlayer("Andrey", Role.BOSS)
    player.roleName should be ("Босс")
  }
  
  test("canBeKilledByMafia method"){
    var player = makePlayer("Andrey", Role.BOSS)
    player.canBeKilledByMafia should be (false)
    player = makePlayer("Andrey", Role.MAFIA)
    player.canBeKilledByMafia should be (false)
    player = makePlayer("Andrey", Role.CHILD)
    player.canBeKilledByMafia should be (false)
    player = makePlayer("Andrey", Role.CHILD_GIRL)
    player.canBeKilledByMafia should be (false)
    player = makePlayer("Andrey", Role.KOMISSAR)
    player.canBeKilledByMafia should be (true)
  }
  
  test("addAchievement method"){
    val player = makePlayer("Andrey", Role.BOSS)
    player.achievements.size should be (0)
    player.achievementScore should be (0.0)
    player.addAchievement(new Achievement(1.2, "Test"))
    player.achievements.size should be (1)
    player.achievementScore should be (1.2)
    player.addAchievement(new Achievement( 2.7434, "Test2"))
    player.achievements.size should be (2)
    player.achievementScore should be (3.94 +- 0.001)
  }
  
  test("isInRole method"){
    val player = makePlayer("Andrey", Role.BOSS)
    intercept[IllegalArgumentException]{
      player.isInRole(null)
    }
    player.isInRole(Role.BOSS) should be (true)
    checkIsOnlyInRole(player, Role.BOSS)
    player.isInRole(Role.MAFIA) should be (false)
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    komissar.isInRole(Role.KOMISSAR) should be (true)
    checkIsOnlyInRole(komissar, Role.KOMISSAR)
    komissar.isInRole(Role.MAFIA) should be (false)
  }
  
  test("isMafia method"){
    val citizen = makePlayer("Andrey", Role.CITIZEN)
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val child = makePlayer("Andrey", Role.CHILD)
    val childWoman = makePlayer("Andrey", Role.CHILD_GIRL)
    val serzhant = makePlayer("Andrey", Role.SERZHANT)
    val doctor = makePlayer("Andrey", Role.DOCTOR)
    val boss = makePlayer("Andrey", Role.BOSS)
    val mafia = makePlayer("Andrey", Role.MAFIA)
    val maniac = makePlayer("Andrey", Role.MANIAC)
    citizen.isMafia should be (false)
    komissar.isMafia should be (false)
    child.isMafia should be (false)
    childWoman.isMafia should be (false)
    serzhant.isMafia should be (false)
    doctor.isMafia should be (false)
    boss.isMafia should be (true)
    mafia.isMafia should be (true)
    maniac.isMafia should be (false)
  }
  
  test("isDoctor method"){
    val citizen = makePlayer("Andrey", Role.CITIZEN)
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val child = makePlayer("Andrey", Role.CHILD)
    val childWoman = makePlayer("Andrey", Role.CHILD_GIRL)
    val serzhant = makePlayer("Andrey", Role.SERZHANT)
    val doctor = makePlayer("Andrey", Role.DOCTOR)
    val boss = makePlayer("Andrey", Role.BOSS)
    val mafia = makePlayer("Andrey", Role.MAFIA)
    val maniac = makePlayer("Andrey", Role.MANIAC)
    citizen.isDoctor should be (false)
    komissar.isDoctor should be (false)
    child.isDoctor should be (false)
    childWoman.isDoctor should be (false)
    serzhant.isDoctor should be (false)
    doctor.isDoctor should be (true)
    boss.isDoctor should be (false)
    mafia.isDoctor should be (false)
    maniac.isDoctor should be (false)
  }
  
  test("isManiac method"){
    val citizen = makePlayer("Andrey", Role.CITIZEN)
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val child = makePlayer("Andrey", Role.CHILD)
    val childWoman = makePlayer("Andrey", Role.CHILD_GIRL)
    val serzhant = makePlayer("Andrey", Role.SERZHANT)
    val doctor = makePlayer("Andrey", Role.DOCTOR)
    val boss = makePlayer("Andrey", Role.BOSS)
    val mafia = makePlayer("Andrey", Role.MAFIA)
    val maniac = makePlayer("Andrey", Role.MANIAC)
    citizen.isManiac should be (false)
    komissar.isManiac should be (false)
    child.isManiac should be (false)
    childWoman.isManiac should be (false)
    serzhant.isManiac should be (false)
    doctor.isManiac should be (false)
    boss.isManiac should be (false)
    mafia.isManiac should be (false)
    maniac.isManiac should be (true)
  }
  
  test("isBoss method"){
    val citizen = makePlayer("Andrey", Role.CITIZEN)
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val child = makePlayer("Andrey", Role.CHILD)
    val childWoman = makePlayer("Andrey", Role.CHILD_GIRL)
    val serzhant = makePlayer("Andrey", Role.SERZHANT)
    val doctor = makePlayer("Andrey", Role.DOCTOR)
    val boss = makePlayer("Andrey", Role.BOSS)
    val mafia = makePlayer("Andrey", Role.MAFIA)
    val maniac = makePlayer("Andrey", Role.MANIAC)
    citizen.isBoss should be (false)
    komissar.isBoss should be (false)
    child.isBoss should be (false)
    childWoman.isBoss should be (false)
    serzhant.isBoss should be (false)
    doctor.isBoss should be (false)
    boss.isBoss should be (true)
    mafia.isBoss should be (false)
    maniac.isBoss should be (false)
  }
  
  test("isKomissar method"){
    val citizen = makePlayer("Andrey", Role.CITIZEN)
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val child = makePlayer("Andrey", Role.CHILD)
    val childWoman = makePlayer("Andrey", Role.CHILD_GIRL)
    val serzhant = makePlayer("Andrey", Role.SERZHANT)
    val doctor = makePlayer("Andrey", Role.DOCTOR)
    val boss = makePlayer("Andrey", Role.BOSS)
    val mafia = makePlayer("Andrey", Role.MAFIA)
    val maniac = makePlayer("Andrey", Role.MANIAC)
    citizen.isKomissar should be (false)
    komissar.isKomissar should be (true)
    child.isKomissar should be (false)
    childWoman.isKomissar should be (false)
    serzhant.isKomissar should be (true)
    doctor.isKomissar should be (false)
    boss.isKomissar should be (false)
    mafia.isKomissar should be (false)
    maniac.isKomissar should be (false)
  }
  
  test("isCitizen method"){
    val citizen = makePlayer("Andrey", Role.CITIZEN)
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val child = makePlayer("Andrey", Role.CHILD)
    val childWoman = makePlayer("Andrey", Role.CHILD_GIRL)
    val serzhant = makePlayer("Andrey", Role.SERZHANT)
    val doctor = makePlayer("Andrey", Role.DOCTOR)
    val boss = makePlayer("Andrey", Role.BOSS)
    val mafia = makePlayer("Andrey", Role.MAFIA)
    val maniac = makePlayer("Andrey", Role.MANIAC)
    citizen.isCitizen should be (true)
    komissar.isCitizen should be (false)
    child.isCitizen should be (true)
    childWoman.isCitizen should be (true)
    serzhant.isCitizen should be (false)
    doctor.isCitizen should be (false)
    boss.isCitizen should be (false)
    mafia.isCitizen should be (false)
    maniac.isCitizen should be (false)
  }
  
  test("isNegative method"){
    val citizen = makePlayer("Andrey", Role.CITIZEN)
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val child = makePlayer("Andrey", Role.CHILD)
    val childWoman = makePlayer("Andrey", Role.CHILD_GIRL)
    val serzhant = makePlayer("Andrey", Role.SERZHANT)
    val doctor = makePlayer("Andrey", Role.DOCTOR)
    val boss = makePlayer("Andrey", Role.BOSS)
    val mafia = makePlayer("Andrey", Role.MAFIA)
    val maniac = makePlayer("Andrey", Role.MANIAC)
    citizen.isNegative should be (false)
    komissar.isNegative should be (false)
    child.isNegative should be (false)
    childWoman.isNegative should be (false)
    serzhant.isNegative should be (false)
    doctor.isNegative should be (false)
    boss.isNegative should be (true)
    mafia.isNegative should be (true)
    maniac.isNegative should be (true)
  }
  
  test("isPositive method"){
    val citizen = makePlayer("Andrey", Role.CITIZEN)
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val child = makePlayer("Andrey", Role.CHILD)
    val childWoman = makePlayer("Andrey", Role.CHILD_GIRL)
    val serzhant = makePlayer("Andrey", Role.SERZHANT)
    val doctor = makePlayer("Andrey", Role.DOCTOR)
    val boss = makePlayer("Andrey", Role.BOSS)
    val mafia = makePlayer("Andrey", Role.MAFIA)
    val maniac = makePlayer("Andrey", Role.MANIAC)
    citizen.isPositive should be (true)
    komissar.isPositive should be (true)
    child.isPositive should be (true)
    childWoman.isPositive should be (true)
    serzhant.isPositive should be (true)
    doctor.isPositive should be (true)
    boss.isPositive should be (false)
    mafia.isPositive should be (false)
    maniac.isPositive should be (false)
  }
  
  test("isRole method"){
    val citizen = makePlayer("Andrey", Role.CITIZEN)
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val child = makePlayer("Andrey", Role.CHILD)
    val childWoman = makePlayer("Andrey", Role.CHILD_GIRL)
    val serzhant = makePlayer("Andrey", Role.SERZHANT)
    val doctor = makePlayer("Andrey", Role.DOCTOR)
    val boss = makePlayer("Andrey", Role.BOSS)
    val mafia = makePlayer("Andrey", Role.MAFIA)
    val maniac = makePlayer("Andrey", Role.MANIAC)
    citizen.isRole should be (false)
    komissar.isRole should be (true)
    child.isRole should be (true)
    childWoman.isRole should be (true)
    serzhant.isRole should be (true)
    doctor.isRole should be (true)
    boss.isRole should be (true)
    mafia.isRole should be (true)
    maniac.isRole should be (true)
  }
  
  private def checkIsOnlyInRole(player:Player, role:Role) = {
    Role.filter(role).foreach { r => if(player.isInRole(r)) throw new IllegalArgumentException(role.role) }
  }
}