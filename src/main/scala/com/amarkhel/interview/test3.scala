package com.amarkhel.interview

object test3 extends App {
  object Solution {
    def solution(a: Array[Int]): Int = {
      val list = a.toList
      val sum = a.sum

      def loop(l: List[Int], prev: Int): Int = l match {
        case head :: tail => {
          val prevSum = prev
          val afterSum = sum - prevSum - head
          if (prevSum == afterSum) l.size else loop(tail, prevSum+head)
        }
        case _ => -1

      }
      val equilInd = loop(list, 0)
      if (equilInd == -1) -1 else list.size - equilInd
    }

  }
  
  val arr = Array[Int](-1,3,-4, 5, 1, -6, 2, 1)
  println(Solution.solution(arr))
}