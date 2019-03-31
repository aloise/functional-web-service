package name.aloise.core

import shapeless.union.Union
import shapeless.{HList, HNil}

trait Dep[A] { self =>

  type Requires <: HList

  def provide(d: Requires): A


  def map[B](f: A => B): Dep[B] = new Dep[B]{ x =>
    override type Requires = self.Requires

    override def provide(d: Requires): B = f(self.provide(d))
  }

  def flatMap[B, Req2, BothReq](f: A => Dep.Aux[Req2, ])

    override type Requires = BothReq
  }
}


object Dep {
  type Aux[Req0, A0] = Dep[A0]{ type Requires = Req0}

}