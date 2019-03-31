package name.aloise.core

import shapeless.ops.hlist.Prepend

trait Dep[A] { self =>

  type Requires

  def provide(d: Requires): A

  def map[B](f: A => B): Dep[B] = new Dep[B]{ x =>
    override type Requires = self.Requires

    override def provide(d: Requires): B = f(self.provide(d))
  }

  // todo - hlist union

  def flatMap[B, Req2, BothReq](f: A => Dep.Aux[Req2, B])
                               (implicit merger: Merger.Aux[self.Requires, Req2, BothReq]): Dep.Aux[BothReq, B] = new Dep[B]{

    override type Requires = BothReq

    override def provide(d: BothReq): B = {
      val (d0, d1) = merger.split
      f(self.provide(d0)).provide(d1)
    }
  }
}


trait Merger[D0, D1] {

  type Out

  def merge(dep1: D0, dep2: D1): Out

  def split: (D0, D1)

}

object Merger {


  type Aux[D0, D1, Out2] = Merger[D0, D1]{ type Out = Out2}

  import shapeless.HList

  implicit def implicitHListMerger[D0 <: HList, D1 <: HList, Result <: HList](implicit prepend: Prepend.Aux[D0, D1, Result]): Merger.Aux[D0, D1, Result] = new Merger[D0, D1] {
    override type Out = Result

    override def merge(dep1: D0, dep2: D1): Out = prepend(dep1, dep2)

    override def split: (D0, D1) = ???
  }

}


object Dep {
  type Aux[Req0, A0] = Dep[A0]{ type Requires = Req0}

}