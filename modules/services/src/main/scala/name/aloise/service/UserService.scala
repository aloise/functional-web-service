package name.aloise.service

import name.aloise.models._
import cats.tagless._
import cats._
import cats.effect._
import cats.effect.concurrent.{MVar, Ref}
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
import name.aloise.service.UserService.UserDraft

import scala.util.Random

trait UserService[F[_]] {
  def login(email: Email, password: Password): F[Option[User]]
  def create(user: UserDraft): F[User]
  def remove(userId: UserId): F[Boolean]
  def update(user: User): F[UserId]
  def get(userId: UserId): F[Option[User]]
  def insertRandom(email: Long => String): F[User]
}

trait DoobieUserService extends DoobieServiceHelper {
  import doobie.refined.implicits._
  import eu.timepit.refined._
  import eu.timepit.refined.api.Refined
  import eu.timepit.refined.auto._
  import eu.timepit.refined.numeric._

  case class UserRecord(user: User, password: Password)

  def userServiceDoobieImpl[F[_]: Sync](transactor: Transactor[F]): UserService[F] =
    new UserService[F] {

      private object Transactions {

        def login(email: Email, password: Password) =
          sql"SELECT id, email FROM users WHERE (email = ${email.value}) AND (password = crypt(${password.value}, password))".query[User]

        def get(id: UserId) =
          sql"""SELECT id, email FROM users WHERE id = $id""".query[User]

        def create(email: Email, password: Password) =
          sql"INSERT INTO users(email, password) VALUES (${email.value}, crypt(${password.value}, gen_salt('bf')))".update

        def remove(id: UserId) =
          sql"DELETE FROM users WHERE id = ${id.value}".update

        def updateEmail(userId: UserId, email: Email) =
          sql"UPDATE users SET email = ${email.value} WHERE id = ${userId.value}".update

        def updatePassword(userId: UserId, password: Password) =
          sql"UPDATE users SET password = crypt(${password.value}, gen_salt('bf'))) WHERE id = ${userId.value}".update
      }

      override def login(email: Email, password: Password): F[Option[User]] =
        Transactions.login(email, password).option.transact(transactor)

      override def create(user: UserDraft): F[User] =
        (for {
          userId <- Transactions
            .create(user.email, user.password)
            .withUniqueGeneratedKeys[UserId]("id")
          user <- Transactions.get(userId).unique
        } yield user).transact(transactor)

      override def get(userId: UserId): F[Option[User]] =
        Transactions.get(userId).option.transact(transactor)

      override def remove(userId: UserId): F[Boolean] =
        Transactions.remove(userId).run.map(_>0).transact(transactor)

      override def update(user: User): F[UserId] = ???

      def insertRandom(emailGen: Long => String): F[User] = {
        val rnd          = Random.nextLong()
        val Right(email) = refineV[MatchesEmail](emailGen(rnd))
        create(UserDraft(email, Password("pass" + rnd)))
      }
    }

  def userServiceMemoryImpl[F[_] : Concurrent: Sync](usersRef: Ref[F, Map[UserId, UserRecord]]): UserService[F] = new UserService[F] {
    private val F = implicitly[Sync[F]]
    private implicit def orderingUserId(implicit ord: Ordering[Int]): Ordering[UserId] =
      (x: UserId, y: UserId) => ord.compare(x.value, y.value)


    override def login(email: Email, password: Password): F[Option[User]] =
      for {
        users <- usersRef.get
        userOpt = users.valuesIterator.find(usr => ( usr.user.email == email ) && (usr.password == password))
      } yield userOpt.map(_.user)

    override def create(userDraft: UserDraft): F[User] =
      for {
        users <- usersRef.get
        existing = users.exists( _._2.user.email == userDraft.email)
        _ <- if(existing) F.raiseError(new IllegalArgumentException("User exists")) else F.pure(Unit)
        maxId = if(users.isEmpty) 0 else users.keysIterator.max.value
        newId = UserId(maxId+1)
        user = User(newId, userDraft.email)
        updatedUsers = users + ( newId -> UserRecord(user, userDraft.password) )
        _ <- usersRef.set(updatedUsers)

      } yield user

    override def remove(userId: UserId): F[Boolean] =
      for {
        users <- usersRef.get
        exists = users.contains(userId)
        _ <- usersRef.set(users - userId)
      } yield exists

    override def update(user: User): F[UserId] = ???
    override def get(userId: UserId): F[Option[User]] =
      for {
        users <- usersRef.get
      } yield users.get(userId).map(_.user)

    def insertRandom(emailGen: Long => String): F[User] = {
      val rnd          = Random.nextLong()
      val Right(email) = refineV[MatchesEmail](emailGen(rnd))
      create(UserDraft(email, Password("pass" + rnd)))
    }

  }
}

object UserService extends DoobieUserService {
  case class UserDraft(email: Email, password: Password)
  case class UserLogin(email: Email, password: Password)
}
