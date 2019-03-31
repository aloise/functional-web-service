package name.aloise.core

trait Dep[A] { self =>

  type Requires

  def provide(d: Requires): A

  def map[B](f: A => B): Dep[B] = new Dep[B]{ x =>
    override type Requires = self.Requires

    override def provide(d: Requires): B = f(self.provide(d))
  }

  def flatMap[B, Req2, BothReq](f: A => Dep.Aux[Req2, B])
                               (implicit merger: Merger.Aux[self.Requires, Req2, BothReq]): Dep.Aux[BothReq, B] = new Dep[B]{

    override type Requires = BothReq

    override def provide(d: BothReq): B = {
      val (d0, d1) = merger.split(d)
      f(self.provide(d0)).provide(d1)
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
  type Aux[Req0, A0] = Dep[A0]{ type Requires = Req0}

}