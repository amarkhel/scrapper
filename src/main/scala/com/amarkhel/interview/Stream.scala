package com.amarkhel.interview

sealed trait Stream[+A] {
  def headOption: Option[A] = this match {
    case Empty      => None
    case Cons(h, t) => Some(h())
  }

  def toList: List[A] = {
    @annotation.tailrec
    def go(s: Stream[A], acc: List[A]): List[A] = s match {
      case Cons(h, t) => go(t(), h() :: acc)
      case _          => acc
    }
    go(this, List()).reverse
  }
  
  def takeWhile(p: A => Boolean): Stream[A] = {
    this match {
      case Cons(h, t) if (p(h())) => Stream.cons(h(), t().takeWhile(p))
      case _                      => Stream.empty
    }
  }
  
  def takeWhile2(p: A => Boolean): Stream[A] = {
    foldRight(Stream.empty[A])((a, b) => if(p(a)) Stream.cons(a, b) else Stream.empty)
  }
  
  def hadeOption : Option[A] = {
    foldRight(None: Option[A])((h,_) => Some(h))
  }

  def foldRight[B](z: => B)(f: (A, => B) => B): B =
    this match {
      case Cons(h, t) => f(h(), t().foldRight(z)(f))
      case _          => z
    }

  def forAll(p: A => Boolean): Boolean = foldRight(true)((a, b) => p(a) && b)
  
  def exists(p: A => Boolean): Boolean = this match {
    case Cons(h, t) => p(h()) || t().exists(p)
    case Empty      => false
  }

  def take(n: Int): Stream[A] = this match {
    case Cons(h, t) if n > 1  => Stream.cons(h(), t().take(n - 1))
    case Cons(h, _) if n == 1 => Stream.cons(h(), Stream.empty)
    case _                    => Stream.empty
  }
  
  def map[B] (f : A => B) : Stream [B] = foldRight(Stream.empty[B])( (a, b) => Stream.cons(f(a), b))
  
  def map2[B](f: A => B): Stream[B] =foldRight(Stream.empty[B])((h,t) => Stream.cons(f(h), t)) 

  def constant[A](a: A): Stream[A] = Stream.cons(a, constant(a))
  
  def from(n: Int): Stream[Int] = Stream.cons(n, from(n + 1))
  

  /*
Unlike `take`, `drop` is not incremental. That is, it doesn't generate the
answer lazily. It must traverse the first `n` elements of the stream eagerly.
*/
  @annotation.tailrec
  final def drop(n: Int): Stream[A] = this match {
    case Cons(_, t) if n > 0 => t().drop(n - 1)
    case _                   => this
  }
}
case object Empty extends Stream[Nothing]
case class Cons[+A](h: () => A, t: () => Stream[A]) extends Stream[A]
object Stream {
  def cons[A](hd: => A, tl: => Stream[A]): Stream[A] = {
    lazy val head = hd
    lazy val tail = tl
    Cons(() => head, () => tail)
  }
  def empty[A]: Stream[A] = Empty
  def apply[A](as: A*): Stream[A] =
    if (as.isEmpty) empty else cons(as.head, apply(as.tail: _*))
}

object Run3 extends App {
  println(Stream(1, 2, 3).takeWhile(x => x < 3).toList)
}