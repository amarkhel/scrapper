package com.supergloo

import scala.io.Source

object RatingTroikaApp {
  
  
  def getOverall(name:String) = {
    val lines = Source.fromFile("C:\\Users\\amarkhel\\Downloads\\spark-course-master\\spark-course-master\\spark-streaming\\overall3.txt").getLines()
    val line = lines.find(_.contains(s"[nick]$name[/nick]"))
    val addon = if(line.isDefined) line.get else  s"[tr][td]Не определен[/td][td][nick]$name[/nick][/td][td]-[/td][td]-[/td][/tr]"
    println(s"[spoiler=Рейтинг троечников Общий $name]")

   println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Ник[/th][th]Число игр[/th][th]КПД[/th][/tr]")
    println(addon)
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
  
  def getAlibi(name:String) = {
    val lines = Source.fromFile("C:\\Users\\amarkhel\\Downloads\\spark-course-master\\spark-course-master\\spark-streaming\\alibi.txt").getLines()
    val line = lines.find(_.contains(s"[nick]$name[/nick]"))
    val addon = if(line.isDefined) line.get else  s"[tr][td]Не определен[/td][td][nick]$name[/nick][/td][td]-[/td][td]-[/td][td]-[/td][td]-[/td][td]-[/td][/tr]"
    println(s"[spoiler=Рейтинг алибщиков $name]")

   println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Ник[/th][th]Число игр[/th][th]Число дневных ходов[/th][th]Число ходов в напарника[/th][th]Процент алиби за игру[/th][th]Процент алиби по ходам[/th][/tr]")
    println(addon)
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
  
  def getCitizen(name:String) = {
    val line = Source.fromFile("citizen3.txt").getLines().find(_.contains(s"[nick]$name[/nick]"))
    val addon = if(line.isDefined) line.get else s"[tr][td]Не определен[/td][td][nick]$name[/nick][/td][td]-[/td][td]-[/td][td]-[/td][td]-[/td][td]-[/td][/tr]"
    println(s"[spoiler=Рейтинг троечников роль честный $name]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Ник[/th][th]Число игр[/th][th]КПД[/th][th]Побед[/th][th]Проигрышей[/th][th]Омонов[/th][/tr]")
    println(addon)
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
  
  def getMafia(name:String) = {
    val line = Source.fromFile("mafia3.txt").getLines().find(_.contains(s"[nick]$name[/nick]"))
    val addon = if(line.isDefined) line.get else s"[tr][td]Не определен[/td][td][nick]$name[/nick][/td][td]-[/td][td]-[/td][td]-[/td][td]-[/td][td]-[/td][/tr]"
    println(s"[spoiler=Рейтинг троечников роль мафия $name]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Ник[/th][th]Число игр[/th][th]КПД[/th][th]Побед[/th][th]Проигрышей[/th][th]Омонов[/th][/tr]")
    println(addon)
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }
  
  def getPersonal(name:String) = {
    getOverall(name)
    getMafia(name)
    getCitizen(name)
    getAlibi(name)
  }
  
  def main(args: Array[String]) ={
    /*getPersonal("Картошка")
    getPersonal("prizrock")
    getPersonal("KillHips")
    getPersonal("rimus")
    getPersonal("Вилли Токарев")
    getPersonal("Mark")
    getPersonal("Flur")
    getPersonal("Vera")
    getPersonal("Envy")
    getPersonal("Веро")
    getPersonal("Unstoppable")
    getPersonal("Намико")
    getPersonal("injected")
    getPersonal("Желтый Майк")
    getPersonal("Lion")
    getPersonal("Kallocain")
    getPersonal("BlueeyesKitten")
    getPersonal("фей хуа")
    getPersonal("A La Magnifique")
    getPersonal("3лОй кОт")
    getPersonal("lost")
    getPersonal("Bazinga")
    getPersonal("Jerry Tough")
    getPersonal("Jack Puffon")*/
    //getPersonal("chrome")
    //getPersonal("Miracle-man")
    //getPersonal("takotorayaeva")
    getPersonal("Aspiring")
    
  }
}