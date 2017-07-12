package com.amarkhel.interview

import scala.annotation.tailrec

sealed trait ListFP[+A]
case object NilFP extends ListFP[Nothing]
case class ConsFP[+A](head: A, tail: ListFP[A]) extends ListFP[A]
object ListFP {
  def sum(ints: ListFP[Int]): Int = ints match {
    case NilFP       => 0
    case ConsFP(x, xs) => x + sum(xs)
  }
  def product(ds: ListFP[Double]): Double = ds match {
    case NilFP        => 1.0
    case ConsFP(0.0, _) => 0.0
    case ConsFP(x, xs)  => x * product(xs)
  }
  def apply[A](as: A*): ListFP[A] =
    if (as.isEmpty) NilFP
    else ConsFP(as.head, apply(as.tail: _*))

  def tail[A](l: ListFP[A]) = l match {
    case NilFP            => sys.error("")
    case ConsFP(head, tail) => tail
  }

  def setHead[A](l: ListFP[A], a: A) = l match {
    case NilFP      => sys.error("")
    case ConsFP(_, t) => ConsFP(a, t)
  }

  def drop[A](l: ListFP[A], n: Int): ListFP[A] = {
    if (n > 0)
      l match {
        case NilFP      => NilFP
        case ConsFP(h, t) => drop(t, n - 1)
      }
    else l
  }

  def dropWhile[A](l: ListFP[A], f: A => Boolean): ListFP[A] = {
    l match {
      case NilFP      => NilFP
      case ConsFP(h, t) => if (f(h)) dropWhile(t, f) else l
    }
  }
  def foldRight[A, B](as: ListFP[A], z: B)(f: (A, B) => B): B =
    as match {
      case NilFP       => z
      case ConsFP(x, xs) => f(x, foldRight(xs, z)(f))
    }

  def foldLeft[A, B](as: ListFP[A], z: B)(f: (B, A) => B): B = {
    @tailrec
    def loop(as: ListFP[A], acc: B): B = {
      as match {
        case NilFP       => acc
        case ConsFP(x, xs) => val t = f(acc, x); loop(xs, t)
      }
    }
    loop(as, z)
  }

  def sum2(ns: ListFP[Int]) =
    foldRight(ns, 0)((x, y) => x + y)
  def product2(ns: ListFP[Double]) =
    foldRight(ns, 1.0)(_ * _)

  def length[A](as: ListFP[A]): Int = foldLeft(as, 0)((x, y) => x + 1)

  def sum3(ns: ListFP[Int]) =
    foldLeft(ns, 0)((x, y) => x + y)
  def product3(ns: ListFP[Double]) =
    foldLeft(ns, 1.0)(_ * _)

  def reverse[A](as: ListFP[A]) = foldLeft(as, NilFP: ListFP[A])((x, y) => ConsFP(y, x))

  def foldRight2[A, B](as: ListFP[A], z: B)(f: (A, B) => B): B =
    foldLeft(as, z)((a, b) => f(b, a))

  def foldLeft2[A, B](as: ListFP[A], z: B)(f: (B, A) => B): B =
    foldRight(as, z)((a, b) => f(b, a))

  def append[A](l: ListFP[A], r: ListFP[A]): ListFP[A] = foldRight(l, r)(ConsFP(_,_))

  def concat[A](as: ListFP[ListFP[A]]) : ListFP[A] = {
    foldRight(as, ListFP[A]())(append)
  }
  
  def inc(as: ListFP[Int]) : ListFP[Int] = foldRight(as, ListFP[Int]())( (x, y) => ConsFP(x + 1, y ))
  
  def toStr(as: ListFP[Double]) : ListFP[String] = foldRight(as, ListFP[String]())( (x, y) => ConsFP(x.toString, y ))
  
  def map[A,B](as: ListFP[A])(f: A => B): ListFP[B] = foldRight(as, ListFP[B]())((x, y) => ConsFP(f(x), y))
  
  def filter[A](as: ListFP[A])(f: A => Boolean): ListFP[A] = foldRight(as, ListFP[A]())((x, y) => { val t = f(x); if (t) ConsFP(x, y) else y})
  
  def flatMap[A,B](as: ListFP[A])(f: A => ListFP[B]): ListFP[B] = foldRight(as, ListFP[B]())((x, y) => append(f(x), y))
  
  def zipWith[A, B](as: ListFP[A], ab: ListFP[B]) : ListFP[(A, B)] = (as, ab) match {
    case (_, NilFP) => NilFP
    case (NilFP, _) => NilFP
    case (ConsFP(h, t), ConsFP(h2, t2)) => ConsFP((h, h2), zipWith(t, t2))
  }
  
}

object Evaluator extends App {
  println(ListFP.foldRight(ListFP(1, 2, 3), NilFP: ListFP[Int])(ConsFP(_, _)))

  println(ListFP.reverse(ListFP(1, 2, 3)))

  println(ListFP.concat(ListFP(ListFP(1, 2, 3), ListFP(4))))
  
  println(ListFP.map(ListFP(1, 2, 3))( _ + 5))
  
  println(ListFP.filter(ListFP(1, 2, 3))( _ % 2 == 1))
}