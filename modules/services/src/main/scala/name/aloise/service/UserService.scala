package name.aloise.service

import name.aloise.models.UserId

trait UserService[F[_]] {
  def login[User](email: String, password: String): F[User]

  def create[User](email: String, password: String): F[UserId]
}
