package com.amarkhel.interview

object test4 extends App {
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
    val right3 = new Tree(12, null, null)
    val left3 = new Tree(2, null, null)
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
    val right3 = new Tree(12, null, null)
    val left3 = new Tree(2, null, null)
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

      def loopLeft(t: Tree, acc: Int, max: Int): Int = {
        t.l match {
          case null => t.r match {
            case null => List[Int](acc, max).max
            case b: Tree => {
              val maxi = if (acc > max) acc else max
              loopLeft(b, 0, maxi)
            }
          }
          case a: Tree => loopLeft(a, acc + 1, max)
        }
      }

      def loopRight(t: Tree, acc: Int, max: Int): Int = {
        t.r match {
          case null => t.l match {
            case null => List[Int](acc, max).max
            case b: Tree => {
              val maxi = if (acc > max) acc else max
              loopRight(b, 0, maxi)
            }
          }
          case a: Tree => loopRight(a, acc + 1, max)
        }
      }

      val maxLeft = loopLeft(t.l, 1, 1)
      val maxRight = loopLeft(t.r, 0, 1)
      val maxLeft2 = loopRight(t.l, 0, 1)
      val maxRight2 = loopRight(t.r, 1, 1)
      List(maxLeft, maxRight, maxLeft2, maxRight2).max
    }
  }
}