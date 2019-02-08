package name.aloise.server

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder

object AppServer extends IOApp {

  val helloWorldService = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name.")
  }.orNotFound

  val server: Resource[IO, Server[IO]] = BlazeServerBuilder[IO]
    .bindHttp(8080, "localhost")
    .withHttpApp(helloWorldService)
    .resource

  def run(args: List[String]): IO[ExitCode] =
    server.use(_ => IO.never).map(_ => ExitCode.Success)

}
