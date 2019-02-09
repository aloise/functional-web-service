package name.aloise.service.http

import cats.effect.{Async, Sync}
import io.circe.ObjectEncoder
import org.http4s.HttpRoutes
import io.circe.syntax._
import name.aloise.build.BuildInfo
import HealthHttpService.Health
import io.circe.generic.semiauto.deriveEncoder


case class HealthHttpService[F[_] : Async]() extends ApiHttpService[F]{
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