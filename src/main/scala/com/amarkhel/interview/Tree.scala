package com.amarkhel.interview

sealed trait Tree[+A]
case class Leaf[A](value: A) extends Tree[A]
case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

object Tree {

  def size[A](t: Tree[A]): Int = {
    t match {
      case Leaf(_)      => 1
      case Branch(l, r) => size(l) + size(r) + 1
    }
  }

  def max(t: Tree[Int]): Int = {
    def loop(tr: Tree[Int], acc: Int) = {
      t match {
        case Leaf(x)      => x max acc
        case Branch(l, r) => max(l) max max(r)
      }
    }
    loop(t, Int.MinValue)
  }

  def depth[A](t: Tree[A]): Int = {
    def loop(tr: Tree[A], acc: Int) = {
      t match {
        case Leaf(x)      => 1
        case Branch(l, r) => (depth(l) max depth(r)) + 1
      }
    }
    loop(t, 0)
  }
  
  def map[A, B](t:Tree[A])(f : A => B) : Tree[B] = {
    t match {
        case Leaf(x)      => Leaf(f(x))
        case Branch(l, r) => Branch(map(l)(f), map(r)(f))
      }
  }
  
  def fold[A,B](t: Tree[A])(f: A => B)(g: (B,B) => B): B = {
    t match {
        case Leaf(x)      => f(x)
        case Branch(l, r) => g(fold(l)(f)(g), fold(r)(f)(g))
      }
  }
}

object Run extends App {
  val t:Tree[Int] = Branch(Branch(Leaf(1), Branch(Leaf(2), Leaf(3))), Branch(Leaf(5), Leaf(6)))
  println(Tree.size(t))
  
  println(Tree.max(t))
  
  t match {
    case Leaf(x)      => println(x)
    case Branch(l, r) => println(Tree.max(l));  println(Tree.max(r))
  }
  
  println(Tree.depth(t))
  
  t match {
    case Leaf(x)      => println(x)
    case Branch(l, r) => println(Tree.depth(l));  println(Tree.depth(r))
  }
  
  println(Tree.map(t)( _*2.1))
}