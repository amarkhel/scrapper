package com.amarkhel.interview

sealed trait Option[+A] {
  def map[B](f: A => B): Option[B] = this match {
    case None    => None
    case Some(x) => Some(f(x))
  }
  def flatMap[B](f: A => Option[B]): Option[B] = this match {
    case None    => None
    case Some(x) => f(x)
  }
  def getOrElse[B >: A](default: => B): B = this match {
    case None    => default
    case Some(x) => x
  }
  def orElse[B >: A](ob: => Option[B]): Option[B] = this match {
    case None    => ob
    case Some(x) => Some(x)
  }
  def filter(f: A => Boolean): Option[A] = this match {
    case None              => None
    case Some(x) if (f(x)) => Some(x)
  }

  def map2[A, B, C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C] = {
    a flatMap (x => b map (y => f(x, y)))
  }

  def traverse[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] = {
    a match {
      case Nil    => Some(Nil)
      case h :: t => map2(f(h), traverse(t)(f))(_ :: _)
    }
  }

  def traverse1[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] = {
    a.foldRight[Option[List[B]]](Some(List.empty))((a, b) => map2(f(a), b)(_ :: _))
  }
}
case class Some[+A](get: A) extends Option[A]
case object None extends Option[Nothing]

sealed trait Either[+E, +A] {
  def map[B](f: A => B): Either[E, B] = this match {
    case Left(e) => Left(e)
    case Right(r) => Right(f(r))
  }
  def flatMap[EE >: E, B](f: A => Either[EE, B]): Either[EE, B] =
  this match {
    case Left(e) => Left(e)
    case Right(a) => f(a)
  }
  def orElse[EE >: E, B >: A](b: => Either[EE, B]): Either[EE, B] = this match {
    case Left(e) => b
    case Right(_) => this
  }
  def map2[EE >: E, B, C](b: Either[EE, B])(f: (A, B) => C): Either[EE, C] = {
    this flatMap ( a => b map (ab => f (a, ab)))
  }

  def traverse[E, A, B](as: List[A])(
    f: A => Either[E, B]): Either[E, List[B]] = {
    as match {
      case Nil    => Right(Nil)
      case h :: t => (f(h) map2 traverse(t)(f))(_ :: _)
    }
  }
}
case class Left[+E](value: E) extends Either[E, Nothing]
case class Right[+A](value: A) extends Either[Nothing, A]

object Run2 extends App {
  def mean(xs: Seq[Double]): Option[Double] =
    if (xs.isEmpty) None
    else Some(xs.sum / xs.length)

  def variance(xs: Seq[Double]): Option[Double] = {
    mean(xs) flatMap (m => mean(xs.map(x => Math.pow(x - m, 2))))
  }

  /*def sequence[A](a: List[Option[A]]): Option[List[A]] = {
    a match {
      case x :: Nil => 
    }
  }*/

}