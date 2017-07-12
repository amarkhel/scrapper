package webscrapper.service

sealed trait Tree[+A]
case class Leaf[A](value: A) extends Tree[A]
case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]


object Tree {

  def size[A](tree:Tree[A]):Int = {
    tree match {
      case Leaf(v) => 1
      case Branch(left, right) => size(left) + size(right)
    }
  }
  
  def maximum(tree:Tree[Int]) : Int = {
    tree match  {
      case Leaf(v) => v
      case Branch(left, right) => maximum(left).max(maximum(right))
    }
  }
  
  def depth[A](tree:Tree[A]) : Int = {
    tree match  {
      case Leaf(v) => 1
      case Branch(left, right) => depth(left) + 1 max (depth(right) + 1 )
    }
  }
  
  def map[A,B](tree:Tree[A])(f: A=>B) : Tree[B] = {
    tree match  {
      case Leaf(v) => new Leaf(f(v))
      case Branch(left, right) => new Branch(map(left)(f), map(right)(f))
    }
  }
  
  def flatMap[A,B](tree:Tree[A])(f: A=>Tree[B]) : Tree[B] = {
    tree match  {
      case Leaf(v) => f(v)
      case Branch(left, right) => new Branch(flatMap(left)(f), flatMap(right)(f))
    }
  }
  
  def fold[A,B](tree:Tree[A], acc:B)(f:(A,B) => B) : Tree[B] = {
    tree match  {
      case Leaf(v) => new Leaf(f(v, acc))
      case Branch(left, right) => new Branch(fold(left, acc)(f), fold(right,acc)(f))
    }
  }
  
 /* def size2[A](tree:Tree[A]):Int = {
    fold(tree, 0)((tree, elem) => elem + 1)
  }*/
  
}