package webscrapper

import webscrapper.util.RussianStemmer
import webscrapper.util.Stemmer

case class Message(val target: Player, val content: String, val smiles: List[String], val timeFromStart:Int) {
  require(content != null)

  def fromRole = target.role
  def hasSmiles = countSmiles > 0

  def text = content
  
  def toStem = stem(removeNicks(removeStopWords(normalise())))
  
  def rem = removeNicks(removeStopWords(normalise()))
      
  def toNorm = this.copy(content = removeFrequent(stem(removeNicks(removeStopWords(normalise())))))
  
  def toNormalise = this.copy(content = normalise())
  
  def author = target.name
  
  def removeFrequent(text:String) = modify(text, filter = RussianStemmer.generalWords)

  def removeStopWords(text:String) = modify(text, filter = RussianStemmer.stopWords)
  
  def removeNicks(text:String) = modify(text, filter = RussianStemmer.stopNicks)
  
  def stem(text:String) = modify(text, how = _.map(Stemmer.stem))
  
  private def modify(text:String, how: Array[String] => Array[String] = identity, filter:List[String] = Nil) = {
    val words = text.split(" ")
    val transformed = filter match {
      case Nil => how(words)
      case list => filterOp(words, filter)
    }
    transformed.mkString(" ")
  }
  
  private def filterOp(arr : Array[String], filt:List[String]) = arr.filter(!filt.contains(_))
  
  def from(player: Player) = {
    require(player != null)
    target == player
  }

  def hasSmileFrom(player: Player) = hasSmiles && from(player)

  def countSmiles = smiles.size

  def countSmilesFrom(player: Player) = {
    require(player != null)
    if (from(player)) countSmiles else 0
  }

  def normalise(predicate:Player=>Boolean = (player:Player) => true) = {
    if(predicate(target)){
      toRoleTextPair.map(_._2).mkString(" ")
    } else ""
  }
  
  def transform = toRoleTextPair
  
  private def toRoleTextPair = {
    val patterns = List("""http([^\s-]+)gif""", "<img src=", "alt=", ">", "[^a-zA-Zа-яА-Я ]")
    val replaced = patterns.foldLeft(content)((a, b) => a.replaceAll(b, "")).trim
    val splitted = replaced.split(" ")
    val filtered = for (word <- splitted if (!word.startsWith("to") && word.length > 2)) yield (target.role -> word)
    filtered
  }
}