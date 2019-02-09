package name.aloise.service.http

import cats.effect.{Async, ContextShift}
import org.http4s.{HttpRoutes, StaticFile}

import scala.concurrent.ExecutionContext

case class FaviconHttpService[F[_] : Async : ContextShift](blockingFileAccessEC: ExecutionContext) extends ApiHttpService[F] {
  override val routes: HttpRoutes[F] = HttpRoutes.of {
    case req @ GET -> Root / "favicon.ico" =>
      StaticFile.fromResource("/favicon/favicon-32x32.png", blockingFileAccessEC, Some(req)).getOrElseF(NotFound())
  }
}
