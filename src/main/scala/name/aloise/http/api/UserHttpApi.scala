package name.aloise.http.api

import cats.effect.Async
import cats.implicits._
import io.circe.{Decoder, Encoder, HCursor, Json}
import name.aloise.models.{Email, Password, User, UserId}
import name.aloise.service.UserService
import org.http4s.HttpRoutes
import io.circe.generic.semiauto._
import io.circe.syntax._

import scala.util.Try
import org.http4s.circe._

case class UserHttpApi[F[_] : Async](service: UserService[F]) extends HttpApi[F] with UserHttpApiHelper {


  override val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / UserIdVar(userId) =>
      Ok(service.get(userId).map(_.asJson))

    // case POST -> Root =>


  }

}

trait UserHttpApiHelper {

  import io.circe.refined._

  implicit lazy val emailEncoder = deriveEncoder[Email]
  implicit lazy val emailDecoder = deriveDecoder[Email]

  implicit lazy val passwordDecoder = deriveDecoder[Password]
  implicit lazy val passwordEncoder = deriveEncoder[Password]


  implicit val userIdEncoder: Encoder[UserId] = (a: UserId) => Json.fromInt(a.value)
  implicit val userIdDecoder: Decoder[UserId] = (c: HCursor) => c.as[Int].map(UserId)
  implicit lazy val userEncoder: Encoder[User] = deriveEncoder[User]
  implicit lazy val userDecoder: Decoder[User] = deriveDecoder[User]

  object UserIdVar {
    def unapply(str: String): Option[UserId] =
      Try(str.toInt).map(UserId).toOption
  }

}