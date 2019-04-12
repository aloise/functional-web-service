package name.aloise.core.utils.hlist

import shapeless.{::, HList, HNil}
import shapeless.ops.hlist.{FilterNot, Remove}

/**
 * Splitting type C into A and B
 *
 * @tparam L left side
 * @tparam R right side
 * @tparam C combined
 */
trait Detach[L, R, C] {
  def apply(c: C): (L, R)
}


trait LowPriorityDetach {
  implicit def hnilDetach[M <: HList]: Detach[HNil, M, M] = (c: M) => (HNil, c)
}

trait MediumPriorityDetach {
  implicit def hlistDetach[A, T <: HList, M <: HList, U <: HList](
                                                                   implicit detachedTail: Detach[T, M, U],
                                                                   filtered: FilterNot.Aux[M, A, M]
                                                                 ): Detach[A :: T, M, A :: U] = (c: A :: U) => {
    val (tail, data) = detachedTail(c.tail)
    (c.head :: tail, data)
  }
}

object Detach extends MediumPriorityDetach with LowPriorityDetach {

  def apply[A, B, C](implicit ev: Detach[A, B, C]): Detach[A, B, C] = ev

  implicit def hlistDetachUnfiltered[H, T <: HList, M <: HList, MR <: HList, U <: HList](
      implicit removed: Remove.Aux[M, H, (H, MR)],
      detached: Detach[T, MR, U]
  ): Detach[H :: T, M, H :: U] = (c: H :: U) => {
    val (t, mr) = detached(c.tail)
    (c.head :: t, removed.reinsert((c.head, mr)))
  }

}