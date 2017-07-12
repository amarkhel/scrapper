/*package webscrapper.service

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

sealed trait List[+A] // `List` data type, parameterized on a type, `A`
case object Nil extends List[Nothing] // A `List` data constructor representing the empty list
 Another data constructor, representing nonempty lists. Note that `tail` is another `List[A]`,
which may be `Nil` or another `Cons`.
 
case class Cons[+A](head: A, tail: List[A]) extends List[A]

object List { // `List` companion object. Contains functions for creating and working with lists.
  def sum(ints: List[Int]): Int = ints match { // A function that uses pattern matching to add up a list of integers
    case Nil => 0 // The sum of the empty list is 0.
    case Cons(x,xs) => x + sum(xs) // The sum of a list starting with `x` is `x` plus the sum of the rest of the list.
  }

  def product(ds: List[Double]): Double = ds match {
    case Nil => 1.0
    case Cons(0.0, _) => 0.0
    case Cons(x,xs) => x * product(xs)
  }
  
  def apply[A](as: A*): List[A] = // Variadic function syntax
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))

  val x = List(1,2,3,4,5) match {
    case Cons(x, Cons(2, Cons(4, _))) => x
    case Nil => 42
    case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
    case Cons(h, t) => h + sum(t)
    case _ => 101
  }

  def append[A](a1: List[A], a2: List[A]): List[A] =
    a1 match {
      case Nil => a2
      case Cons(h,t) => Cons(h, append(t, a2))
    }

  def foldRight[A,B](as: List[A], z: B)(f: (A, B) => B): B = // Utility functions
    as match {
      case Nil => z
      case Cons(x, xs) => f(x, foldRight(xs, z)(f))
    }

  def sum2(ns: List[Int]) =
    foldRight(ns, 0)((x,y) => x + y)

  def product2(ns: List[Double]) =
    foldRight(ns, 1.0)(_ * _) // `_ * _` is more concise notation for `(x,y) => x * y`; see sidebar


  def length[A](as: List[A]) = foldRight(as, 0)((_, acc) => acc + 1)
  
  def foldLeft[A,B](as: List[A], z: B)(f: (A, B) => B): B = {
    @tailrec
    def loop(as: List[A], z: B, acc:B) : B = {
      as match {
      case Nil => acc
      case Cons(x, xs) => loop(xs, z, f(x, acc))
    }
    }
    loop(as, z, z)
  }
  @tailrec
  def foldLeft2[A,B](as: List[A], z: B)(f: (A, B) => B): B = {
      as match {
      case Nil => z
      case Cons(x, xs) => foldLeft2(xs, f(x, z))(f)
    }
  }
  
  def foldRight2[A,B](as: List[A], z: B)(f: (A, B) => B): B = {
    as match {
      case Nil => 
      case Cons(x, xs) => loop(xs, z, f(x, acc))
  }
  
  def reverse[A](as:List[A]) : List[A] = {
    foldLeft(as, List[A]())((list, elem) => Cons(list, elem))
  }
  
  def add_1(as:List[Int]) : List[Int] = {
    foldLeft(as, List[Int]())((elem, list) => Cons(elem + 1, list))
  }
  
  def add(as:List[Int], count:Int) : List[Int] = {
    foldLeft(as, List[Int]())((elem, list) => Cons(elem + count, list))
  }
  
  def intToStr(as:List[Int]) : List[String] = {
    foldLeft(as, List[String]())((elem, list) => Cons(elem.toString, list))
  }
  
  def map[A,B](as:List[A], f:A=>B) : List[B] = {
    foldLeft(as, List[B]())((elem, list) => Cons(f(elem), list))
  }
  
  def flatMap[A,B](as:List[A], f:A=>List[B]) : List[B] = {
    foldRight(as, List[B]())((elem, list) => List.append(list, f(elem)))
  }
  
  def filter[A](as:List[A], f:A=>Boolean) : List[A] = {
    foldRight(as, List[A]())((h, t) => if(f(h)) Cons(h,t) else t)
    
  }
  
  def head[A](l: List[A]): A =
    l match {
      case Nil => sys.error("tail of empty list")
      case Cons(x,t) => x
    }
  
  def concat(f:List[Int], g:List[Int]) : List[Int] = (f,g) match {
    case (Nil, _) => Nil
    case (x, Nil) => x
    case (Cons(x, xt), Cons(y, yt)) => Cons(x + y, concat(xt, yt))
  }
  
  def zipWith[A](f:List[A], g:List[A])(op: (A, A) => A) : List[A] = (f,g) match {
    case (Nil, _) => Nil
    case (x, Nil) => x
    case (Cons(x, xt), Cons(y, yt)) => Cons(op(x, y), zipWith(xt, yt)(op))
  }
  
  def hasSubsequence[A](sup:List[A], sub:List[A]) :Boolean = {
    sub match {
      case Nil => true
      case Cons(x, xt) => false
    }
  }
  
  def filterLeft[A](as:List[A], f:A=>Boolean) : List[A] = {
    foldLeft(as, List[A]())((h, t) => if(f(h)) Cons(h,t) else t)
    
  }
  
  3. The third case is the first that matches, with `x` bound to 1 and `y` bound to 2.
  

  
  Although we could return `Nil` when the input list is empty, we choose to throw an exception instead. This is
  a somewhat subjective choice. In our experience, taking the tail of an empty list is often a bug, and silently
  returning a value just means this bug will be discovered later, further from the place where it was introduced.
  It's generally good practice when pattern matching to use `_` for any variables you don't intend to use on the
  right hand side of a pattern. This makes it clear the value isn't relevant.
  
  def tail[A](l: List[A]): List[A] =
    l match {
      case Nil => sys.error("tail of empty list")
      case Cons(_,t) => t
    }

  
  If a function body consists solely of a match expression, we'll often put the match on the same line as the
  function signature, rather than introducing another level of nesting.
  
  def setHead[A](l: List[A], h: A): List[A] = l match {
    case Nil => sys.error("setHead on empty list")
    case Cons(_,t) => Cons(h,t)
  }

  
  Again, it's somewhat subjective whether to throw an exception when asked to drop more elements than the list
  contains. The usual default for `drop` is not to throw an exception, since it's typically used in cases where this
  is not indicative of a programming error. If you pay attention to how you use `drop`, it's often in cases where the
  length of the input list is unknown, and the number of elements to be dropped is being computed from something else.
  If `drop` threw an exception, we'd have to first compute or check the length and only drop up to that many elements.
  
  def drop[A](l: List[A], n: Int): List[A] =
    if (n <= 0) l
    else l match {
      case Nil => Nil
      case Cons(_,t) => drop(t, n-1)
    }

  
  Somewhat overkill, but to illustrate the feature we're using a _pattern guard_, to only match a `Cons` whose head
  satisfies our predicate, `f`. The syntax is to add `if <cond>` after the pattern, before the `=>`, where `<cond>` can
  use any of the variables introduced by the pattern.
  
  def dropWhile[A](l: List[A], f: A => Boolean): List[A] =
    l match {
      case Cons(h,t) if f(h) => dropWhile(t, f)
      case _ => l
    }

  
  Note that we're copying the entire list up until the last element. Besides being inefficient, the natural recursive
  solution will use a stack frame for each element of the list, which can lead to stack overflows for
  large lists (can you see why?). With lists, it's common to use a temporary, mutable buffer internal to the
  function (with lazy lists or streams, which we discuss in chapter 5, we don't normally do this). So long as the
  buffer is allocated internal to the function, the mutation is not observable and RT is preserved.
  Another common convention is to accumulate the output list in reverse order, then reverse it at the end, which
  doesn't require even local mutation. We'll write a reverse function later in this chapter.
  
  def init[A](l: List[A]): List[A] =
    l match {
      case Nil => sys.error("init of empty list")
      case Cons(_,Nil) => Nil
      case Cons(h,t) => Cons(h,init(t))
    }
  def init2[A](l: List[A]): List[A] = {
    import collection.mutable.ListBuffer
    val buf = new ListBuffer[A]
    @annotation.tailrec
    def go(cur: List[A]): List[A] = cur match {
      case Nil => sys.error("init of empty list")
      case Cons(_,Nil) => List(buf.toList: _*)
      case Cons(h,t) => buf += h; go(t)
    }
    go(l)
  }
}*/