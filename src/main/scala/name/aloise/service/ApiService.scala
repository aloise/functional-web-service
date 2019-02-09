package name.aloise.service

import cats.effect.Async
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

abstract class ApiService[F[_] : Async] extends Http4sDsl[F] {
  def routes: HttpRoutes[F]
}
