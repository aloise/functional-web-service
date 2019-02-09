package name.aloise.service

import cats.effect.Sync
import io.circe.ObjectEncoder
import org.http4s.HttpRoutes
import org.http4s._
import io.circe.syntax._
import org.http4s.dsl.io._
import name.aloise.build.BuildInfo
import name.aloise.service.HealthService.Health
import io.circe.generic.semiauto.deriveEncoder

case class HealthService[F[_] : Sync]() {

  private val healthResponse = Health(BuildInfo.name, BuildInfo.version).asJson

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root =>
      Ok("123") //
  }
}

case object HealthService {
  case class Health(service: String, version: String, health: Boolean = true)
  case object Health {
    implicit val encoder: ObjectEncoder[Health] = deriveEncoder[Health]
  }
}