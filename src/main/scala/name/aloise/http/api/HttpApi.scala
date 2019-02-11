package name.aloise.http.api

import cats.effect.Async
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

abstract class HttpApi[F[_] : Async] extends Http4sDsl[F] {
  def routes: HttpRoutes[F]
}