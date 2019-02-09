package name.aloise.service.http

import cats.effect.Async
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

abstract class ApiHttpService[F[_] : Async] extends Http4sDsl[F] {
  def routes: HttpRoutes[F]
}
