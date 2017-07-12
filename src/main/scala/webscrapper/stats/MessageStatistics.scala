package webscrapper.stats

import webscrapper.Player
import webscrapper.Round
import scala.collection.mutable
import webscrapper.PlayerStatisticQueryResult
import webscrapper.util.StatUtils._
import java.time.Duration

class MessageStatistics(val players: List[Player], val rounds: List[Round], val duration: Long) {
  type Map[T, E] = mutable.HashMap[T, E]
  val playerRounds = new Map[Player, List[Round]]
  val countMessagesForPlayer = new Map[Player, Int]
  val countSmileMessagesForPlayer = new Map[Player, Int]
  val countSmilesForPlayer = new Map[Player, Int]
  val countMessages = rounds.map(_.countMessages).sum
  val countSmileMessages = rounds.map(_.countSmileMessages).sum
  val countSmiles = rounds.map(_.countSmiles).sum
  players.foreach(populate(_))
  val maxMessagesFrom = max(countMessagesForPlayer.toMap)
  val minMessagesFrom = min(countMessagesForPlayer.toMap)
  val maxSmilesFrom = max(countSmilesForPlayer.toMap)
  val minSmilesFrom = min(countSmilesForPlayer.toMap)
  val roundMessages = new Map[Round, Int]
  rounds.foreach((r: Round) => roundMessages.put(r, r.countMessages))

  def from(player:Player) = messages.filter(_.from(player))
  
  /**
   * @param player
   * @return count messages from that player
   */
  def countMessagesFrom(player: Player) = countMessagesForPlayer(player)

  /**
   * @param player
   * @return count messages that contains smiles from that player
   */
  def countSmileMessagesFrom(player: Player) = countSmileMessagesForPlayer(player)

  /**
   * @param player
   * @return count smiles from that player
   */
  def countSmilesFrom(player: Player) = countSmilesForPlayer(player)

  /**
   * @param countRounds
   * @return average count of messages from initial round to countRounds parameter
   */
  def avgMessagesRound(countRounds: Long) = rounds.filter(_.order < countRounds).map((r: Round) => r.countMessages / r.countPlayers).sum / countRounds

  /**
   * @return messages per minute metric. Count of messages
   * divided by duration of the game
   */
  def messagesPerMinute = 1.0 * countMessages / (duration / 60)

  /**
   * @param player
   * @return average count per round for that player
   */
  def averageMessages(player: Player) = {
    val countAliveRounds = countRoundsForPlayer(player)
    val average = avgMessagesRound(countAliveRounds)
    val playerAvg = avgMessagesRoundForPlayer(player)
    0.1 * (playerAvg - average)
  }

  /**
   * @return average count of messages per round
   * Calculates as count of all messages divided by count of rounds
   */
  def avgMessagesRound = 1.0 * countMessages / countRounds

  def avgMessagesRoundForPlayer(player: Player) = 1.0 * countMessagesFrom(player) / countRoundsForPlayer(player)

  def countRoundsForPlayer(player: Player) = playerRounds(player).size

  def countRounds = rounds.size
  
  def populate(player: Player) = {
    val roundsPlayed = rounds.filter(_.alived(player)).toList
    playerRounds.put(player, roundsPlayed);
    countMessagesForPlayer.put(player, roundsPlayed.map(_.countMessagesFrom(player)).sum)
    countSmileMessagesForPlayer.put(player, roundsPlayed.map(_.countSmileMessagesFrom(player)).sum)
    countSmilesForPlayer.put(player, roundsPlayed.map(_.countSmilesFrom(player)).sum)
  }

  def messagesPerMinute(player: Player) = {
    val countRounds = countRoundsForPlayer(player)
    val start = rounds(0).startedAt
    val finish = rounds(countRounds - 1).finishedAt
    val duration = Duration.between(start, finish)
    val seconds = duration.getSeconds
    1.0 * countMessagesFrom(player) / (seconds / 60)
  }

  def smiles = rounds.map(_.smiles).flatten.toList
  
  def messages = rounds.map(_.messages).flatten.toList
}