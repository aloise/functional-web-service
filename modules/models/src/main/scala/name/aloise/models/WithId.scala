package name.aloise.models


trait Id[OBJ, X <: Id[OBJ, X]] { self: X =>
  def value: Int
}

trait WithId[OBJ, X <: Id[OBJ, X]] { self: OBJ =>
  def id: X
}
