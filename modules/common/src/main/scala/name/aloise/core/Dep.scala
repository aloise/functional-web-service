package name.aloise.core

import cats.{FlatMap, Functor, Id}

trait DepT[F[_], A] { self =>

  type Requires

  def run(d: Requires): F[A]

  def map[B](f: A => B)(implicit functorF: Functor[F]): DepT.Aux[Requires, F, B] = new DepT[F, B] { x =>
    override type Requires = self.Requires
    override def run(d: Requires): F[B] = functorF.map(self.run(d))(f)
  }

  def flatMap[B, Req2, BothReq](f: A => DepT.Aux[Req2, F, B])(
      implicit merger: Merger.Aux[self.Requires, Req2, BothReq],
      flatMapF: FlatMap[F]): DepT.Aux[BothReq, F, B] = new DepT[F, B] {

    override type Requires = BothReq

    override def run(d: BothReq): F[B] = {
      val (d0, d1) = merger.split(d)
      flatMapF.flatMap(self.run(d0))(a => f(a).run(d1))
    }
  }

}

object DepT {
  type Aux[Env, F[_], A] = DepT[F, A] { type Requires = Env }
}

trait Dep[A] extends DepT[Id, A]

abstract class Merger[D0, D1] {

  type Out

  def merge(dep1: D0, dep2: D1): Out

  def split(merged: Out): (D0, D1)

}

object Merger {

  type Aux[D0, D1, Out2] = Merger[D0, D1] { type Out = Out2 }

  import shapeless.HList
  import shapeless.ops.hlist._
  import shapeless.Nat

  object HList {

    implicit def implicitHListMerger[D1 <: HList, D2 <: HList, Result <: HList, N <: Nat](
        implicit prependOp: Prepend.Aux[D1, D2, Result],
        splitOp: Split.Aux[Result, N, D1, D2]
    ): Merger.Aux[D1, D2, Result] = new Merger[D1, D2] {
      override type Out = Result

      override def merge(dep1: D1, dep2: D2): Out = prependOp(dep1, dep2)

      override def split(merged: Out): (D1, D2) = splitOp(merged)
    }
  }

  object HListUnion {
    implicit def implicitHListUnionMerger[D1 <: HList, D2 <: HList, Result <: HList, N <: Nat](
        implicit unionOp: Union.Aux[D1, D2, Result],
        intersectionD1: Intersection.Aux[Result, D1, D1],
        intersectionD2: Intersection.Aux[Result, D2, D2]
    ): Merger.Aux[D1, D2, Result] = new Merger[D1, D2] {
      override type Out = Result

      override def merge(dep1: D1, dep2: D2): Out = unionOp(dep1, dep2)

      override def split(merged: Out): (D1, D2) = (intersectionD1(merged), intersectionD2(merged))
    }
  }

}

object Dep {

  import shapeless._
  import Merger.HListUnion._

  import java.io.File

  type Aux[Req0, A0] = DepT.Aux[Req0, cats.Id, A0]

  def pure[A](value: A): Dep.Aux[HNil, A] = new Dep[A] {
    override type Requires = HNil

    override def run(d: Requires): A = value
  }

  def reader[Env, A](f: Env => A): Dep.Aux[Env :: HNil, A] = new Dep[A] {
    override type Requires = Env :: HNil

    override def run(d: Requires): A = f(d.head)
  }

  def run2[ENV, HLIST <: HList, A](depMonad: Dep.Aux[HLIST, A])(env: ENV)(
      implicit gen: Generic.Aux[ENV, HLIST]): A =
    depMonad.run(gen.to(env))

  def run2[A](depMonad: Dep.Aux[HNil, A]): A =
    depMonad.run(HNil)

  import scala.language.existentials

  val buildEnv = for {
    c  <- reader((name: String) => name.length)
    d2 <- reader((name2: String) => name2.length * 2)
    d3 <- reader((name3: String) => name3.length * 3)
    a  <- pure(1)
    b  <- pure(2)
    x = 5
    d <- reader((bool: Boolean) => bool)
    e <- reader((marko: List[Int]) => marko.length)
    //
  } yield c + a + b + x + d2 + e + d3

  val anotherEnv = for {
    x <- reader((f: File) => 15)
  } yield x

  val serviceEnv = for {
    b1 <- buildEnv
    b2 <- anotherEnv
  } yield b1 + b2

  case class Environment(name: String, isTrue: Boolean, marko: List[Int])

  val deps = Environment("aloise", isTrue = true, List(1, 2, 3))

  run2(buildEnv)(deps)
  run2(buildEnv)("aloise", true, List(1, 2, 3))

  run2(pure(1))

  run2(serviceEnv)(("aloise", true, List(1, 2, 3), new File("123")))

  println(buildEnv)

}
