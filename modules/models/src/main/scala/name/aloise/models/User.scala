package name.aloise.models


final case class UserId(value: Int) extends Id[User, UserId]

case class User(id: UserId, email: Email) extends WithId[User, UserId]