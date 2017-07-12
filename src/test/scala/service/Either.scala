/*package webscrapper.service

import scala.{ Option => _, Either => _, _ }
sealed trait Either[+E, +A]{
  def map[B](f: A => B): Either[E, B] = this match {
    case Right(a) => Right(f(a))
    case Left(b) => Left(b)
  }
def flatMap[EE >: E, B](f: A => Either[EE, B]): Either[EE, B] = {
  this match {
    case Right(a) => f(a)
    case Left(b) => Left(b)
  }
}
def sequence[E, A](es: List[Either[E, A]]): Either[E, List[A]] = {
  es.foldRight(List())(op)
}
def traverse[E, A, B](as: List[A])(
f: A => Either[E, B]): Either[E, List[B]]

def map2[EE >: E, B, C](b: Either[EE, B])(f: (A, B) => C):
Either[EE, C] = {
  for (a1 <- this;
  b1 <- b) yield f(a1, b1)
}
}
case class Left[+E](value: E) extends Either[E, Nothing]
case class Right[+A](value: A) extends Either[Nothing, A]*/