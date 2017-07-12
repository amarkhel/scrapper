package webscrapper

case class SmileHolder(val smile:String, val count:Int){
  require(!smile.isEmpty)
}