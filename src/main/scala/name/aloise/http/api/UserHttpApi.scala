package name.aloise.http.api

import cats.effect.Async
import cats.implicits._
import io.circe.{Decoder, Encoder, HCursor, Json}
import name.aloise.models.{Email, Password, User, UserId}
import name.aloise.service.UserService
import org.http4s.HttpRoutes
import io.circe.syntax._
import name.aloise.service.UserService.{UserDraft, UserLogin}

import scala.util.Try
import org.http4s.circe._

case class UserHttpApi[F[_] : Async](service: UserService[F]) extends HttpApi[F] {
  import UserHttpApiHelper._

  implicit val userDraftEntityDecoder = jsonOf[F, UserDraft]
  implicit val loginDraftEntityDecoder = jsonOf[F, UserLogin]

  override val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / UserIdVar(userId) =>
      Ok(service.get(userId).map(_.asJson))

    case GET -> Root / "random" =>
      Ok(service.insertRandom(l => "test"+l+"@test.com").map(_.asJson))

    case req @ POST -> Root =>
      for {
        userDraft <- req.as[UserDraft]
        user <- service.create(userDraft)
        resp <- Ok(user.asJson)
      } yield resp

    case req @ POST -> Root / "login" =>
      for {
        login <- req.as[UserLogin]
        user <- service.login(login.email, login.password)
        resp <- Ok(user.asJson)
      } yield resp
  }

}

object UserHttpApiHelper {

  import eu.timepit.refined.auto._
  import io.circe.refined._
  import io.circe.generic.extras.semiauto.{deriveUnwrappedDecoder, deriveUnwrappedEncoder}
  import io.circe.generic.semiauto._

  implicit val passwordDecoder: Decoder[Password] = deriveUnwrappedDecoder[Password]
  implicit val passwordEncoder: Encoder[Password] = deriveUnwrappedEncoder[Password]

  implicit val userIdEncoder: Encoder[UserId] = (a: UserId) => Json.fromInt(a.value)
  implicit val userIdDecoder: Decoder[UserId] = (c: HCursor) => c.as[Int].map(UserId)
  implicit val userEncoder: Encoder[User] = deriveEncoder[User]
  implicit val userDecoder: Decoder[User] = deriveDecoder[User]
  implicit val userDraftDecoder: Decoder[UserDraft] = deriveDecoder[UserDraft]
  implicit val userLoginDecoder: Decoder[UserLogin] = deriveDecoder[UserLogin]

  object UserIdVar {
    def unapply(str: String): Option[UserId] =
      Try(str.toInt).map(UserId).toOption
  }

}