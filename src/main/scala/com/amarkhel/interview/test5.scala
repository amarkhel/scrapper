package com.amarkhel.interview

object test5 extends App {
  import scala.collection.JavaConverters._
  class Tree(var x: Int, var l: Tree, var r: Tree)
  // you can write to stdout for debugging purposes, e.g.
  // println("this is a debug message")
  def makeTree() = {
    val right2 = new Tree(20, null, null)
    val left2 = new Tree(21, null, null)
    val left1 = new Tree(12, left2, right2)
    val left = new Tree(3, null, null)
    val right = new Tree(10, left1, null)
    val t = new Tree(5, left, right)
    t
  }

  def makeTree3() = {
    //val right2 = new Tree(1, left5, right5)
    val right3 = new Tree(2, null, null)
    val left3 = new Tree(12, null, null)
    val left4 = new Tree(3, null, null)
    val left2 = new Tree(1, null, null)
    val left1 = new Tree(7, left2, null)
    val right1 = new Tree(4, left4, null)
    val left = new Tree(8, left3, right3)
    val right = new Tree(9, left1, right1)
    val t = new Tree(5, left, right)
    t
  }

  def makeTree2() = {
    val left5 = new Tree(12, null, null)
    val right6 = new Tree(0, null, null)
    val right5 = new Tree(7, null, right6)
    val right2 = new Tree(1, left5, right5)
    val right3 = new Tree(2, null, null)
    val left3 = new Tree(12, null, null)
    val left4 = new Tree(3, null, null)
    val left2 = new Tree(1, null, null)
    val left1 = new Tree(7, left2, null)
    val right1 = new Tree(4, left4, right2)
    val left = new Tree(8, left3, right3)
    val right = new Tree(9, left1, right1)
    val t = new Tree(5, left, right)
    t
  }

  val t = makeTree()
  val t2 = makeTree3()
  val t3 = makeTree2()
  println(Solution.solution(t))
  println(Solution.solution(t2))
  println(Solution.solution(t3))

  object Solution {

    def solution(t: Tree): Int = {

      def loop(t: Tree, min: Int, max: Int, amp: Int): Int = {
        val left = t.l match {
          case null => t.r match {
            case null => amp
            case b: Tree => {
              val i = b.x
              val mini = if (i < min) i else min
              val maxi = if (i > max) i else max
              val ampi = if (amp < (maxi - mini)) maxi - mini else amp
              loop(b, mini, maxi, ampi)
            }
          }
          case a: Tree => {
            val i = a.x
            val mini = if (i < min) i else min
            val maxi = if (i > max) i else max
            val ampi = if (amp < (maxi - mini)) maxi - mini else amp
            loop(a, mini, maxi, ampi)
          }
        }
        val right = t.r match {
          case null => t.l match {
            case null => amp
            case b: Tree => {
              val i = b.x
              val mini = if (i < min) i else min
              val maxi = if (i > max) i else max
              val ampi = if (amp < (maxi - mini)) maxi - mini else amp
              loop(b, mini, maxi, ampi)
            }
          }
          case a: Tree => {
            val i = a.x
            val mini = if (i < min) i else min
            val maxi = if (i > max) i else max
            val ampi = if (amp < (maxi - mini)) maxi - mini else amp
            loop(a, mini, maxi, ampi)
          }
        }
        List(left,right).max
      }
      if(t == null) 0 else {
        val maxLeft = loop(t, t.x, t.x, 0)
      maxLeft
      }
      
    }
  }
}