package name.aloise.models


trait Id[OBJ] {
  def value: Long
}

trait WithId[OBJ] {
  self: OBJ =>
  def id: Id[OBJ]
}
