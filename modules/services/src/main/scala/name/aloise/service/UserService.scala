package name.aloise.service

import name.aloise.models.{User, UserId}
import cats.tagless._

@autoFunctorK(true) trait UserService[F[_]] {
  def login(email: String, password: String): F[User]
  def create(user: User): F[UserId]
  def remove(userId: UserId): F[Unit]
  def update(user: User): F[UserId]
}
