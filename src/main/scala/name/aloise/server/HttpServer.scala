package name.aloise.server

import cats.effect._
import org.http4s.HttpRoutes
import org.http4s.implicits._
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder

case class HttpServer[F[_] : ConcurrentEffect : Timer](services: HttpRoutes[F])(host: String = "localhost", port: Int = 8080) {

  val server: Resource[F, Server[F]] = BlazeServerBuilder[F]
    .bindHttp(port, host)
    .withHttpApp(services.orNotFound)
    .withNio2(true)
    .withWebSockets(false)
    .resource

}
