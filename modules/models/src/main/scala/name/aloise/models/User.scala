package name.aloise.models

final case class UserId(value: Int) extends Id[User, UserId]

final case class Password(value: String) extends AnyVal

case class User(id: UserId, email: Email, password: Password) extends WithId[User, UserId]