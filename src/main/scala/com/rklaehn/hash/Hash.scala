package com.rklaehn.hash

import cats.Eq
import simulacrum._
import scala.{ specialized => sp }

@typeclass trait Hash[@sp A] { self =>

  def eqv(a: A, b: A): Boolean

  def hash(a: A): Int

  def on[@sp B](f: B => A): Hash[B] =
    new Hash[B] {
      def eqv(x: B, y: B): Boolean = self.eqv(f(x), f(y))
      def hash(x: B): Int = self.hash(f(x))
    }
}

object Hash {
  implicit def hashIsEq[A](implicit hash: Hash[A]): Eq[A] = new Eq[A] {
    override def eqv(x: A, y: A): Boolean = hash.eqv(x, y)
  }
  implicit val byteHash: Hash[Byte] = new Hash[Byte] {
    override def hash(a: Byte): Int = a.hashCode
    override def eqv(x: Byte, y: Byte): Boolean = x == y
  }
  implicit val shortHash: Hash[Short] = new Hash[Short] {
    override def hash(a: Short): Int = a.hashCode
    override def eqv(x: Short, y: Short): Boolean = x == y
  }
  implicit val intHash: Hash[Int] = new Hash[Int] {
    override def hash(a: Int): Int = a.hashCode
    override def eqv(x: Int, y: Int): Boolean = x == y
  }
  implicit val longHash: Hash[Long] = new Hash[Long] {
    override def hash(a: Long): Int = a.hashCode
    override def eqv(x: Long, y: Long): Boolean = x == y
  }
  implicit val floatHash: Hash[Float] = new Hash[Float] {
    override def hash(a: Float): Int = a.hashCode
    override def eqv(x: Float, y: Float): Boolean = x == y
  }
  implicit val doubleHash: Hash[Double] = new Hash[Double] {
    override def hash(a: Double): Int = a.hashCode
    override def eqv(x: Double, y: Double): Boolean = x == y
  }
  implicit val charHash: Hash[Char] = new Hash[Char] {
    override def hash(a: Char): Int = a.hashCode
    override def eqv(x: Char, y: Char): Boolean = x == y
  }
  implicit val booleanHash: Hash[Boolean] = new Hash[Boolean] {
    override def hash(a: Boolean): Int = a.hashCode
    override def eqv(x: Boolean, y: Boolean): Boolean = x == y
  }
  implicit val stringHash: Hash[String] = new Hash[String] {
    override def hash(a: String): Int = a.hashCode
    override def eqv(x: String, y: String): Boolean = x == y
  }

  /**
   * Convert an implicit `Hash[B]` to an `Hash[A]`
   * using the given function `f`.
   */
  def by[@sp A, @sp B](f: A => B)(implicit ev: Hash[B]): Hash[A] =
    ev.on(f)
}
