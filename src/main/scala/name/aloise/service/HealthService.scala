package name.aloise.service

import cats.effect.{Async, Sync}
import io.circe.ObjectEncoder
import org.http4s.HttpRoutes
import io.circe.syntax._
import name.aloise.build.BuildInfo
import name.aloise.service.HealthService.Health
import io.circe.generic.semiauto.deriveEncoder


case class HealthService[F[_] : Async]() extends ApiService[F]{
  import org.http4s.circe._

  private val healthResponse = Health(BuildInfo.name, BuildInfo.version, BuildInfo.revision).asJson

  override val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root =>
      Ok(healthResponse)
  }
}

case object HealthService {
  case class Health(service: String, version: String, revision: String, health: Boolean = true)
  case object Health {
    implicit val encoder: ObjectEncoder[Health] = deriveEncoder[Health]
  }
}