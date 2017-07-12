package webscrapper

case class User(val name:String){
  require(!name.isEmpty)
}