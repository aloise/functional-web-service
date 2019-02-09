package name.aloise.server

import cats.effect._
import org.http4s.HttpApp
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder

class HttpServer[F[_]: Effect : ConcurrentEffect : Timer](services: HttpApp[F])(host: String = "localhost", port:Int = 8080) {

  val server: Resource[F, Server[F]] = BlazeServerBuilder[F]
    .bindHttp(port, host)
    .withHttpApp(services)
    .withNio2(true)
    .withWebSockets(false)
    .resource

}
