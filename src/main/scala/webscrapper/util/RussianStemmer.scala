package webscrapper.util
import scala.annotation._
import scala.io.Source
import webscrapper.Properties

object RussianStemmer {
  case class Equivalence(val main:String, val synonyms:List[String])
  val equivalences = {
    val lines = Source.fromFile(Properties.path + "synonyms.txt", "UTF-8").getLines.toList
    for(line <- lines;
      val splitted = line.split(":");
      val name = splitted(0);
      val list = splitted(1).split(",").map(_.trim).map(_.toUpperCase).toList
    ) yield new Equivalence(name, list)
  }
  val stopWords = Source.fromFile(Properties.path + "stopwords_ru.txt", "UTF-8").getLines.toList
  val stopWords2 = Source.fromFile(Properties.path + "stop2.txt", "UTF-8").getLines.toList
  val stopNicks = Source.fromFile(Properties.path + "stopnicks.txt", "UTF-8").getLines.toList
  val generalWords = Source.fromFile(Properties.path + "words.txt", "UTF-8").getLines.toList
  
  def stem(word:String, needStem:Boolean=true, useStopLength:Boolean=true, useStopWords:Boolean = true, useStopNicks:Boolean=true):String = {
    if (checkLength(word, useStopLength) || checkStopWords(word, useStopWords) || checkStopNicks(word, useStopNicks)) "" else if(needStem) handleSynonyms(word) else word
  }
  
  def isOk(word:String) = stem(word) != ""
  
  def checkLength(word:String, check:Boolean) = check && word.length <= 2
  def checkStopWords(word:String, useStopWords:Boolean) = useStopWords && stopWords2.contains(word)
  def checkStopNicks(word:String, useStopNicks:Boolean) = useStopNicks && stopNicks.contains(word)
  def handleSynonyms(word:String) = {
    equivalences.find(_.synonyms.contains(word.toUpperCase)) match {
      case None => word
      case Some(x) => x.main
    }
  }                
}