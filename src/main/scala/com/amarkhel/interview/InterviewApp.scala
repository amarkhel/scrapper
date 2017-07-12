package com.amarkhel.interview

import java.time.LocalDateTime
import scala.math.Ordering.Implicits._
import java.time.LocalDate
import scala.annotation.tailrec

object InterviewApp extends App {
  case class Paper(title: String, publicationDate: LocalDate)
  implicit class MyLocalDateImprovements(val ld: LocalDate)
      extends Ordered[LocalDate] {
    def compare(that: LocalDate): Int = ld.compareTo(that)
  }
  //val localDateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)
  def sizeOfPapers(p: List[Paper]) = p.size

  def sortPapersByTitle(p: List[Paper]) = p.sortBy(_.title)

  def sortPapersByPublicationDate(p: List[Paper]) = p.sortBy(_.publicationDate)

  def reverseSortPapersByPublicationDate(p: List[Paper]) = sortPapersByPublicationDate(p).reverse

  def isSorted[A](as: Array[A], ordered: (A, A) => Boolean): Boolean = {
    if (as.size < 2) true else {
      @tailrec
      def loop(l: List[A], prev: A): Boolean = {
        l match {
          case Nil => true
          case head :: tail => {
            if (!ordered(prev, head)) false else loop(tail, head)
          }
        }
      }
      loop(as.tail.toList, as.head)
    }

  }
  
  val list = Array(1, 5, 3, 2,4,6,7)
  val list2 = Array(1, 2,2, 3, 4,5,5,6,7)
  val list3 = Array(7, 6, 5, 4,4,4,3,2,1)
  
  println(isSorted[Int](list, (a, b) => a < b))
  println(isSorted[Int](list, (a, b) => a <= b))
  println(isSorted[Int](list, (a, b) => a > b))
  println(isSorted[Int](list, (a, b) => a >= b))
  println(isSorted[Int](list2, (a, b) => a < b))
  println(isSorted[Int](list2, (a, b) => a <= b))
  println(isSorted[Int](list2, (a, b) => a > b))
  println(isSorted[Int](list2, (a, b) => a >= b))
  println(isSorted[Int](list3, (a, b) => a < b))
  println(isSorted[Int](list3, (a, b) => a <= b))
  println(isSorted[Int](list3, (a, b) => a > b))
  println(isSorted[Int](list3, (a, b) => a >= b))
  
  def curry[A,B,C](f: (A, B) => C): A => (B => C) = f.curried
  def curry2[A,B,C](f: (A, B) => C): A => (B => C) = a => b => f(a, b)
  def uncurry[A,B,C](f: A => B => C): (A, B) => C = (a,b) => f(a)(b)
  
  def compose[A,B,C](f: B => C, g: A => B): A => C =  a => f(g(a))
  
  
}