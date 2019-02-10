package name.aloise.models

final case class UserId(value: Long) extends Id[User]

final case class Password(value: String) extends AnyVal

case class User(id: UserId, email: Email, password: Option[Password]) extends WithId[User]