package name.aloise.service

import name.aloise.models.{User, UserId}
import cats.tagless._
import cats._
import cats.effect._
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor

@finalAlg @autoFunctorK(true) trait UserService[F[_]] {
  def login(email: String, password: String): F[User]
  def create(user: User): F[UserId]
  def remove(userId: UserId): F[Unit]
  def update(user: User): F[UserId]
  def get(userId: UserId): F[Option[User]]
}

trait DoobieUserService extends DoobieServiceHelper {
  import doobie.refined.implicits._

  final private val userTable: String = "users"

  def userServiceDoobieImpl[F[_]: Sync](transactor: Transactor[F]): UserService[F] = new UserService[F] {
      override def login(email: String, password: String): F[User] = ???

      override def create(user: User): F[UserId] =
        sql"INSERT INTO $userTable(email, password) VALUES (${user.email.value.value}, ${user.password.value})".update
          .withUniqueGeneratedKeys[UserId]("id")
          .transact(transactor)

      override def get(userId: UserId): F[Option[User]] =
        sql"SELECT id, email, '******' AS password FROM $userTable WHERE id = $userId"
          .query[User]
          .option
          .transact(transactor)

      override def remove(userId: UserId): F[Unit] = ???
      override def update(user: User): F[UserId]   = ???
    }
}

object UserService extends DoobieUserService
