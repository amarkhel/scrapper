/*package fpinscala.errorhandling

//hide std library `Option` and `Either`, since we are writing our own in this chapter
import scala.{ Option => _, Either => _, _ }

sealed trait Option[+A] {
  
  def mean(xs: Seq[Double]) : Option[Double] = {
    xs match {
      case Nil => None
      case x => Some(x.sum/x.length)
    }
  }

  def variance(xs: Seq[Double]): Option[Double] =
    mean(xs) flatMap (m => mean(xs.map(x => math.pow(x - m, 2))))

    def map2[A,B,C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C] = {
    a flatMap (aa => b map (bb => f(aa, bb)))
  }
  
  def map[B](f: A => B): Option[B] = {
    this match {
      case None => None
      case Some(x) => Some(f(x))
    }
  }
  def flatMap[B](f: A => Option[B]): Option[B] = {
    this match {
      case None => None
      case Some(x) => f(x)
    }
  }
  def getOrElse[B >: A](default: => B): B = {
    this match {
      case None => default
      case Some(x) => x
    }
  }
  def orElse[B >: A](ob: => Option[B]): Option[B] = {
    this match {
      case None => ob
      case Some(x) => Some(x)
    }
  }
  def filter(f: A => Boolean): Option[A] = {
    this match {
      case Some(x) if f(x) => Some(x)
      case _ => None
    }
  }
}

case class Some[+A](get: A) extends Option[A]
case object None extends Option[Nothing]

object Option {
def sequence[A](a: List[Option[A]]): Option[List[A]] =
    a match {
      case Nil => Some(Nil)
      case h :: t => h flatMap (hh => sequence(t) map (hh :: _))
    }
  
  It can also be implemented using `foldRight` and `map2`. The type annotation on `foldRight` is needed here; otherwise
  Scala wrongly infers the result type of the fold as `Some[Nil.type]` and reports a type error (try it!). This is an
  unfortunate consequence of Scala using subtyping to encode algebraic data types.
  
  def sequence_1[A](a: List[Option[A]]): Option[List[A]] =
    a.foldRight[Option[List[A]]](Some(Nil))((x,y) => map2(x,y)(_ :: _))

  def traverse[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] =
    a match {
      case Nil => Some(Nil)
      case h::t => map2(f(h), traverse(t)(f))(_ :: _)
    }

  def traverse_1[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] =
    a.foldRight[Option[List[B]]](Some(Nil))((h,t) => map2(f(h),t)(_ :: _))

  def sequenceViaTraverse[A](a: List[Option[A]]): Option[List[A]] =
    traverse(a)(x => x)
}
}
*/