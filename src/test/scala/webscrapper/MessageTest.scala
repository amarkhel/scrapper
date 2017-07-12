package webscrapper

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import TestUtils._

@RunWith(classOf[JUnitRunner])
class MessageTest extends FunSuite with Matchers {
  
  test("message should have smiles") {
    val player = makePlayer("Andrey")
    val smiles = List("Smile")
    val message = Message(player, "Some text", smiles, 0)
    message.hasSmiles should be (true)
  }
  
  test("message should not have smiles") {
    val player = makePlayer("Andrey")
    val smiles = List()
    val message = Message(player, "Some text", smiles, 0)
    message.hasSmiles should be (false)
  }
  
  test("message method from should work correct") {
    val player = makePlayer("Andrey")
    val smiles = List("Smile")
    val message = Message(player, "Some text", smiles, 0)
    val notExist = makePlayer("Not exist")
    message.from(player) should be (true)
    message.from(notExist) should be (false)
    intercept[IllegalArgumentException]{
      message.from(null)
    }
  }
  
  test("message method hasSmilesFrom should work correct") {
    val player = makePlayer("Andrey")
    val smiles = List("Smile")
    val message = Message(player, "Some text", smiles, 0)
    val notExist = makePlayer("Not exist")
    message.hasSmileFrom(player) should be (true)
    message.hasSmileFrom(notExist) should be (false)
    intercept[IllegalArgumentException]{
      message.hasSmileFrom(null)
    }
    val noSmiles = List()
    val messageWithoutSmiles = Message(player, "Some text", noSmiles, 0)
    messageWithoutSmiles.hasSmileFrom(player) should be (false)
  }
  
  test("message count smiles should return correct result") {
    val player = makePlayer("Andrey")
    val smiles = List("Smile")
    val message = Message(player, "Some text", smiles, 0)
    message.countSmiles should be (1)
    val smiles2 = List()
    val message2 = Message(player, "Some text", smiles2, 0)
    message2.countSmiles should be (0)
    val smiles3 = List("Smile", "Smile2", "Smile3")
    val message3 = Message(player, "Some text", smiles3, 0)
    message3.countSmiles should be (3)
  }
  
  test("count smiles from player should return correct result") {
    val player = makePlayer("Andrey")
    val player2 = makePlayer("Andrey2")
    val smiles = List("Smile")
    val message = Message(player, "Some text", smiles, 0)
    message.countSmilesFrom(player) should be (1)
    val smiles2 = List()
    val message2 = Message(player, "Some text", smiles2, 0)
    message2.countSmilesFrom(player) should be (0)
    val smiles3 = List("Smile", "Smile2", "Smile3")
    val message3 = Message(player2, "Some text", smiles3, 0)
    message3.countSmilesFrom(player) should be (0)
    message3.countSmilesFrom(player2) should be (3)
    intercept[IllegalArgumentException]{
      message3.countSmilesFrom(null)
    }
  }
}