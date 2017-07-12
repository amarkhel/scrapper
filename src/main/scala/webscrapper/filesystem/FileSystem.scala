package webscrapper.filesystem

import org.jsoup.nodes.Document
import java.nio.file.Paths
import java.nio.file.Files
import java.io.BufferedWriter
import java.io.FileWriter
import scala.util.Try
import java.nio.file.Path
import scala.util.Success
import scala.util.Failure
import scala.collection.JavaConversions._
import java.nio.charset.StandardCharsets
import java.nio.file.StandardOpenOption
import scala.io.Source
import webscrapper.GameSource
import webscrapper.Properties

trait FileSystem {

  def saveGameSource(id: Long)(implicit doc: Document) = {
    val text = doc.outerHtml.getBytes(StandardCharsets.UTF_8)
    val path = Paths.get(gamePath(id.toInt))
    writeFile(path, text)
  }

  def writeFile(path: Path, text: Array[Byte]): Try[Path] = {
    if (!Files.exists(path)) {
      Try {
        Files.write(path, text, StandardOpenOption.CREATE)
      }
    } else Success(path)
  }

  //def read(path:String) : List[String] = Source.fromFile(path, "UTF-8").getLines.toList
  
  def readGame(id:Int) = GameSource.fromFile(gameFile(id))
  
  private def gamePath(id:Int) = Properties.gamePath + s"games\\${id}.txt"
  
  private def gameFile(id:Int) = Paths.get(gamePath(id)).toFile
}