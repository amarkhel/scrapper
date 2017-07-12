package com.amarkhel.interview

object test6 extends App {
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

  def makeTree4() = {
    val right1 = new Tree(21, null, null)
    val left2 = new Tree(1, null, null)
    val left1 = new Tree(20, null, null)
    val left = new Tree(3, left1, right1)
    val right = new Tree(10, left2, null)
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
  val t4 = makeTree4()
  println(Solution.solution(t))
  println(Solution.solution(t2))
  println(Solution.solution(t3))
  println(Solution.solution(t4))

  object Solution {

    def solution(t: Tree): Int = {

      var count = 1
      def loop(t: Tree, max: Int): Unit = {
        t.l match {
          case null => ()

          case a: Tree => {
            val i = a.x
            val visible = i >= max
            val maxi = if (i > max) i else max
            if (visible) count = count + 1
            loop(a, maxi)
          }
        }
        t.r match {
          case null => ()

          case a: Tree => {
            val i = a.x
            val visible = i >= max
            val maxi = if (i > max) i else max
            if (visible) count = count + 1
            loop(a, maxi)
          }
        }
        ()
      }
if(t == null) 0 else {
        loop(t, t.x)
      count
      }
      
    }
  }
}