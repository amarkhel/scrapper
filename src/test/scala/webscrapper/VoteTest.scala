package webscrapper

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import TestUtils._

@RunWith(classOf[JUnitRunner])
class VoteTest extends FunSuite with Matchers {
  
  test("Instantiation"){
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val mafia = makePlayer("Vika", Role.MAFIA)
    intercept[IllegalArgumentException]{
      new Vote(null, komissar)
    }
    intercept[IllegalArgumentException]{
      new Vote(komissar, null)
    }
    intercept[IllegalArgumentException]{
      new Vote(null, null)
    }
    intercept[IllegalArgumentException]{
      new Vote(komissar, komissar)
    }
    val vote = new Vote(komissar, mafia)
    vote.target should be (komissar)
    vote.destination should be (mafia)
  }
  
  test("Method from should work correctly"){
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val mafia = makePlayer("Vika", Role.MAFIA)
    val vote = new Vote(komissar, mafia)
    vote.from(komissar) should be (true)
    vote.from(mafia) should be (false)
    intercept[IllegalArgumentException]{
      vote.from(null)
    }
  }
  
  test("Method toMafiaFrom should work correctly"){
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val mafia = makePlayer("Vika", Role.MAFIA)
    val vote = new Vote(komissar, mafia)
    vote.toMafiaFrom(komissar) should be (true)
    vote.toMafiaFrom(mafia) should be (false)
    intercept[IllegalArgumentException]{
      vote.toMafiaFrom(null)
    }
    val citizen = makePlayer("Vika", Role.CITIZEN)
    val vote2 = new Vote(komissar, citizen)
    vote2.toMafiaFrom(komissar) should be (false)
    vote2.toMafiaFrom(mafia) should be (false)
  }
  
  test("Method toMafia should work correctly"){
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val mafia = makePlayer("Vika", Role.MAFIA)
    val vote = new Vote(komissar, mafia)
    vote.toMafia should be (true)
    val citizen = makePlayer("Vika", Role.CITIZEN)
    val vote2 = new Vote(komissar, citizen)
    vote2.toMafia should be (false)
  }
  
  test("Method toImportantGoodRole should work correctly"){
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val mafia = makePlayer("Vika", Role.MAFIA)
    val vote = new Vote(mafia, komissar)
    vote.toImportantGoodRole should be (true)
    val citizen = makePlayer("Serg", Role.CITIZEN)
    val vote2 = new Vote(mafia, citizen)
    vote2.toImportantGoodRole should be (false)
  }
  
  test("Method toRole should work correctly"){
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val mafia = makePlayer("Vika", Role.MAFIA)
    val vote = new Vote(mafia, komissar)
    vote.toRole should be (true)
    val citizen = makePlayer("Serg", Role.CITIZEN)
    val vote2 = new Vote(mafia, citizen)
    vote2.toRole should be (false)
  }
  
  test("Method toRole(role) should work correctly"){
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val mafia = makePlayer("Vika", Role.MAFIA)
    val vote = new Vote(mafia, komissar)
    vote.toRole(Role.KOMISSAR) should be (true)
    vote.toRole(Role.MAFIA) should be (false)
    val citizen = makePlayer("Serg", Role.CITIZEN)
    val vote2 = new Vote(mafia, citizen)
    vote2.toRole(Role.KOMISSAR) should be (false)
    vote2.toRole(Role.CITIZEN) should be (true)
    intercept[IllegalArgumentException]{
      vote.toRole(null)
    }
  }
  
  test("Method fromRole(role) should work correctly"){
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val mafia = makePlayer("Vika", Role.MAFIA)
    val vote = new Vote(mafia, komissar)
    vote.fromRole(Role.KOMISSAR) should be (false)
    vote.fromRole(Role.MAFIA) should be (true)
    val citizen = makePlayer("Serg", Role.CITIZEN)
    val vote2 = new Vote(mafia, citizen)
    vote2.fromRole(Role.KOMISSAR) should be (false)
    vote2.fromRole(Role.CITIZEN) should be (false)
    vote2.fromRole(Role.MAFIA) should be (true)
    intercept[IllegalArgumentException]{
      vote.fromRole(null)
    }
  }
  
  test("Method to(role) should work correctly"){
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val mafia = makePlayer("Vika", Role.MAFIA)
    val vote = new Vote(mafia, komissar)
    vote.to(komissar) should be (true)
    vote.to(mafia) should be (false)
    val citizen = makePlayer("Serg", Role.CITIZEN)
    val vote2 = new Vote(mafia, citizen)
    vote2.to(komissar) should be (false)
    vote2.to(citizen) should be (true)
    vote2.to(mafia) should be (false)
    intercept[IllegalArgumentException]{
      vote.to(null)
    }
  }
  
  test("Method toPlayerFromMafia should work correctly"){
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val mafia = makePlayer("Vika", Role.MAFIA)
    val boss = makePlayer("Vika2", Role.BOSS)
    var vote = new Vote(mafia, komissar)
    vote.toPlayerFromMafia(komissar) should be (true)
    vote.toPlayerFromMafia(mafia) should be (false)
    vote = new Vote(boss, komissar)
    vote.toPlayerFromMafia(komissar) should be (true)
    vote.toPlayerFromMafia(mafia) should be (false)
    vote = new Vote(komissar, mafia)
    vote.toPlayerFromMafia(komissar) should be (false)
    vote.toPlayerFromMafia(mafia) should be (false)
    intercept[IllegalArgumentException]{
      vote.toPlayerFromMafia(null)
    }
  }
  
  test("Method toMafiaFrom should work correctly"){
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val mafia = makePlayer("Vika", Role.MAFIA)
    val boss = makePlayer("Vika2", Role.BOSS)
    var vote = new Vote(mafia, komissar)
    vote.toMafiaFrom(komissar) should be (false)
    vote.toMafiaFrom(mafia) should be (false)
    vote = new Vote(komissar, boss)
    vote.toMafiaFrom(komissar) should be (true)
    vote.toMafiaFrom(mafia) should be (false)
    vote = new Vote(komissar, mafia)
    vote.toMafiaFrom(komissar) should be (true)
    vote.toMafiaFrom(mafia) should be (false)
    intercept[IllegalArgumentException]{
      vote.toMafiaFrom(null)
    }
  }
  
  test("Method fromPlayerToGoodImportantRole should work correctly"){
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val mafia = makePlayer("Vika", Role.MAFIA)
    val boss = makePlayer("Vika2", Role.BOSS)
    val doctor = makePlayer("Vika3", Role.SERZHANT)
    val serzhant = makePlayer("Vika4", Role.DOCTOR)
    var vote = new Vote(mafia, komissar)
    vote.fromPlayerToGoodImportantRole(komissar) should be (false)
    vote.fromPlayerToGoodImportantRole(mafia) should be (true)
    vote = new Vote(mafia, serzhant)
    vote.fromPlayerToGoodImportantRole(komissar) should be (false)
    vote.fromPlayerToGoodImportantRole(mafia) should be (true)
    vote = new Vote(mafia, doctor)
    vote.fromPlayerToGoodImportantRole(komissar) should be (false)
    vote.fromPlayerToGoodImportantRole(mafia) should be (true)
    vote = new Vote(komissar, boss)
    vote.fromPlayerToGoodImportantRole(komissar) should be (false)
    vote.fromPlayerToGoodImportantRole(mafia) should be (false)
    intercept[IllegalArgumentException]{
      vote.toMafiaFrom(null)
    }
  }
  
  test("Method fromPlayerToRole should work correctly"){
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val mafia = makePlayer("Vika", Role.MAFIA)
    val boss = makePlayer("Vika2", Role.BOSS)
    val doctor = makePlayer("Vika3", Role.SERZHANT)
    val serzhant = makePlayer("Vika4", Role.DOCTOR)
    val maniac = makePlayer("Vika5", Role.MANIAC)
    val child = makePlayer("Vika6", Role.CHILD)
    val childW = makePlayer("Vika7", Role.CHILD_GIRL)
    val citizen = makePlayer("Vika7", Role.CITIZEN)
    var vote = new Vote(mafia, komissar)
    vote.fromPlayerToRole(komissar) should be (false)
    vote.fromPlayerToRole(mafia) should be (true)
    vote = new Vote(mafia, serzhant)
    vote.fromPlayerToRole(komissar) should be (false)
    vote.fromPlayerToRole(mafia) should be (true)
    vote = new Vote(mafia, doctor)
    vote.fromPlayerToRole(komissar) should be (false)
    vote.fromPlayerToRole(mafia) should be (true)
    vote = new Vote(komissar, boss)
    vote.fromPlayerToRole(komissar) should be (true)
    vote.fromPlayerToRole(mafia) should be (false)
    vote = new Vote(komissar, mafia)
    vote.fromPlayerToRole(komissar) should be (true)
    vote.fromPlayerToRole(mafia) should be (false)
    vote = new Vote(komissar, child)
    vote.fromPlayerToRole(komissar) should be (true)
    vote.fromPlayerToRole(mafia) should be (false)
    vote = new Vote(komissar, childW)
    vote.fromPlayerToRole(komissar) should be (true)
    vote.fromPlayerToRole(mafia) should be (false)
    vote = new Vote(komissar, maniac)
    vote.fromPlayerToRole(komissar) should be (true)
    vote.fromPlayerToRole(mafia) should be (false)
    vote = new Vote(komissar, citizen)
    vote.fromPlayerToRole(komissar) should be (false)
    vote.fromPlayerToRole(mafia) should be (false)
    intercept[IllegalArgumentException]{
      vote.fromPlayerToRole(null)
    }
  }
  
  test("Method fromPlayerToSpecificRole should work correctly"){
    val komissar = makePlayer("Andrey", Role.KOMISSAR)
    val mafia = makePlayer("Vika", Role.MAFIA)
    val boss = makePlayer("Vika2", Role.BOSS)
    val doctor = makePlayer("Vika3", Role.SERZHANT)
    val serzhant = makePlayer("Vika4", Role.DOCTOR)
    var vote = new Vote(komissar, mafia)
    vote.fromPlayerToSpecificRole(komissar, Role.MAFIA) should be (true)
    vote.fromPlayerToSpecificRole(komissar, Role.BOSS) should be (false)
    vote.fromPlayerToSpecificRole(komissar, Role.DOCTOR) should be (false)
    vote.fromPlayerToSpecificRole(komissar, Role.MANIAC) should be (false)
    vote.fromPlayerToSpecificRole(komissar, Role.CHILD) should be (false)
    vote.fromPlayerToSpecificRole(komissar, Role.KOMISSAR) should be (false)
    vote.fromPlayerToSpecificRole(komissar, Role.SERZHANT) should be (false)
    vote.fromPlayerToSpecificRole(komissar, Role.CHILD_GIRL) should be (false)
    vote.fromPlayerToSpecificRole(komissar, Role.SERZHANT) should be (false)
    intercept[IllegalArgumentException]{
      vote.fromPlayerToSpecificRole(null, null)
    }
    intercept[IllegalArgumentException]{
      vote.fromPlayerToSpecificRole(null, Role.MAFIA)
    }
    intercept[IllegalArgumentException]{
      vote.fromPlayerToSpecificRole(komissar, null)
    }
  }
}