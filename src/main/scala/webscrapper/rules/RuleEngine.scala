package webscrapper.rules

import webscrapper.Game
import org.kie.internal.builder.KnowledgeBuilder
import org.kie.internal.builder.DecisionTableConfiguration
import org.kie.internal.runtime.StatelessKnowledgeSession
import org.kie.internal.builder.DecisionTableInputType
import org.kie.internal.KnowledgeBase
import org.kie.internal.KnowledgeBaseFactory
import org.kie.internal.builder.KnowledgeBuilderFactory
import org.kie.internal.builder.KnowledgeBuilderFactory
import org.kie.api.io.ResourceType
import org.kie.internal.io.ResourceFactory
import scala.collection.JavaConverters._
import webscrapper.TournamentName

trait RuleEngine {
  def calculatePoints(game: Game)(implicit tournamentName:TournamentName) = {
    val pointsPath = tournamentName.name match {
      case "Default" => "points.xls"
      case name:String => name + "/points.xls"
    }
    val knowledgeBase = createKnowledgeBaseFromSpreadsheet(pointsPath)
    val session = knowledgeBase.newStatelessKnowledgeSession

    val statistics = game.statistics
    session.setGlobal("game", game)
    session.setGlobal("statistics", statistics)
    session.setGlobal("voteStats", statistics.voteStats)
    session.setGlobal("messageStats", statistics.messageStats)
    session.execute(game.players.asJava)
  }

  private def createKnowledgeBaseFromSpreadsheet(files: String*) = {
    val dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration
    dtconf.setInputType(DecisionTableInputType.XLS)
    val knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder
    files.foreach { f =>
      knowledgeBuilder.add(ResourceFactory.newClassPathResource(f), ResourceType.DTABLE, dtconf)
    }
    knowledgeBuilder.add(ResourceFactory.newClassPathResource("Achievements.xls"), ResourceType.DTABLE, dtconf)
    if (knowledgeBuilder.hasErrors) 
      throw new RuntimeException(knowledgeBuilder.getErrors.toString)
    else {
      val knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase
      knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages)
      knowledgeBase
    }
  }
}