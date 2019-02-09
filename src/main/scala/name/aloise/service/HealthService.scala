package name.aloise.service

import cats.effect.Sync
import io.circe.ObjectEncoder
import org.http4s.HttpRoutes
import io.circe.syntax._
import name.aloise.build.BuildInfo
import name.aloise.service.HealthService.Health
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.dsl.Http4sDsl


case class HealthService[F[_] : Sync]() extends Http4sDsl[F]{

  import org.http4s.circe._

  private val healthResponse = Health(BuildInfo.name, BuildInfo.version).asJson

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root =>
      Ok(healthResponse)
  }
}

case object HealthService {
  case class Health(service: String, version: String, health: Boolean = true)
  case object Health {
    implicit val encoder: ObjectEncoder[Health] = deriveEncoder[Health]
  }
}