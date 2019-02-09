package name.aloise.service.http

import cats.effect.Async
import io.circe.ObjectEncoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.syntax._
import name.aloise.build.BuildInfo
import name.aloise.service.http.HealthHttpService.Health
import org.http4s.HttpRoutes


case class HealthHttpService[F[_] : Async]() extends ApiHttpService[F] {

  import org.http4s.circe._

  private val healthResponse = Ok(Health(BuildInfo.name, BuildInfo.version, BuildInfo.revision).asJson)

  override val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root =>
      healthResponse
  }
}

case object HealthHttpService {

  case class Health(service: String, version: String, revision: String, health: Boolean = true)

  case object Health {
    implicit val encoder: ObjectEncoder[Health] = deriveEncoder[Health]
  }

}