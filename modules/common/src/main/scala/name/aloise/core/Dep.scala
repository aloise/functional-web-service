package name.aloise.core

trait Dep[A] { self =>

  type Requires

  def run(d: Requires): A

  def map[B](f: A => B): Dep.Aux[Requires, B] = new Dep[B]{ x =>
    override type Requires = self.Requires

    override def run(d: Requires): B = f(self.run(d))
  }

  def flatMap[B, Req2, BothReq](f: A => Dep.Aux[Req2, B])
                               (implicit merger: Merger.Aux[self.Requires, Req2, BothReq]): Dep.Aux[BothReq, B] = new Dep[B]{

    override type Requires = BothReq

    override def run(d: BothReq): B = {
      val (d0, d1) = merger.split(d)
      f(self.run(d0)).run(d1)
    }
  }
}


abstract class Merger[D0, D1] {

  type Out

  def merge(dep1: D0, dep2: D1): Out

  def split(merged: Out): (D0, D1)

}

object Merger {

  type Aux[D0, D1, Out2] = Merger[D0, D1]{ type Out = Out2}

  import shapeless.HList
  import shapeless.ops.hlist.Prepend
  import shapeless.ops.hlist.Split
  import shapeless.ops.hlist.Length
  import shapeless.Nat

  implicit def implicitHListMerger[D0 <: HList, D1 <: HList, Result <: HList, N <: Nat](
      implicit prepend: Prepend.Aux[D0, D1, Result],
      len: Length[D0],
      split: Split.Aux[Result, N, D0, D1]
  ): Merger.Aux[D0, D1, Result] = new Merger[D0, D1] {
    override type Out = Result

    override def merge(dep1: D0, dep2: D1): Out = prepend(dep1, dep2)

    override def split(merged: Out): (D0, D1) = split(merged)
  }

}


object Dep {

  import shapeless._

  type Aux[+Req0, A0] = Dep[A0]{ type Requires = Req0 }

  def pure[A](value: A): Dep.Aux[HList, A] = new Dep[A] {
    type Requires = HNil

    override def run(d: HNil): A = value
  }

  def reader[Env, A](f: Env => A): Dep.Aux[HList, A] = new Dep[A] {
    type Requires = Env :: HNil

    override def run(d: Requires): A = f(d.head)
  }


  val test: Dep.Aux[HNil, Int] = for {
    c <- reader((name:String) => name.length)
    a <- pure(1)
    b <- pure(2)
    //
  } yield a+b+1

  test.run(HNil)

}