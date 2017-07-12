package com.supergloo

case class NamedStat(name:String, stat:Stat, children:Iterable[NamedStat]){
  def count : Int = {
    children match {
      case Nil => stat.count
      case _ => children.map(_.count).sum
    }
  }
  override def toString = s"$name $stat"
}