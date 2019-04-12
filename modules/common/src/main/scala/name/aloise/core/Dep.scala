package name.aloise.core

import cats.{FlatMap, Functor, Id}
import name.aloise.core.utils.hlist.Detach
import shapeless._
import shapeless.ops.hlist.Union
import scala.language.higherKinds

trait DepT[F[_], A] { self =>

  type Env <: HList

  protected def runWithEnv(environment: Env): F[A]

  def run[IN](single: IN)(implicit ev: (IN :: HNil) =:= Env, ne: IN =:!= HList): F[A] =
    runWithEnv(ev(single :: HNil))

  def run[E <: Product](env: E)(implicit gen: Generic.Aux[E, Env]): F[A] =
    runWithEnv(gen.to(env))

  def run(implicit ev: HNil =:= Env): F[A] =
    runWithEnv(ev(HNil))

  def map[B](f: A => B)(implicit functorF: Functor[F], dist: IsDistinctConstraint[Env]): DepT.Aux[Env, F, B] = new DepT[F, B] {
    override type Env = self.Env
    override def runWithEnv(environment: Env): F[B] = functorF.map(self.runWithEnv(environment))(f)
  }

  def flatMap[B, Env2 <: HList, CombinedEnv <: HList](f: A => DepT.Aux[Env2, F, B])(
    implicit unionOp: Union.Aux[Env, Env2, CombinedEnv],
    detach: Detach[Env, Env2, CombinedEnv],
    flatMapF: FlatMap[F]): DepT.Aux[CombinedEnv, F, B] = new DepT[F, B] {

    override type Env = CombinedEnv

    override def runWithEnv(environment: CombinedEnv): F[B] = {
      val (env1, env2) = detach(environment)
      flatMapF.flatMap(self.runWithEnv(env1))(a => f(a).runWithEnv(env2))
    }
  }
}

object DepT {
  type Aux[E, F[_], A] = DepT[F, A] { type Env = E }

  def pure[F[_], A](value: => F[A]): DepT.Aux[HNil, F, A] = new DepT[F, A] {
    override type Env = HNil
    override def runWithEnv(env: Env): F[A] = value
  }

  def inject[F[_]: FlatMap, E, A](reader: E => F[A]): DepT.Aux[E :: HNil, F, A] = new DepT[F, A] {
    override type Env = E :: HNil
    override def runWithEnv(env: Env): F[A] = reader(env.head)
  }
}

trait Dep[A] extends DepT[Id, A]

object Dep {

  type Aux[Req0, A0] = DepT.Aux[Req0, cats.Id, A0]

  def pure[A](value: => A) = DepT.pure[Id, A](value)
  def inject[E, A](reader: E => A) = DepT.inject[Id, E, A](reader)
  
}
