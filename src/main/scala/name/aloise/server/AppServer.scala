package name.aloise.server

import cats.effect._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.Server
import io.chrisdavenport.log4cats.Logger
import org.http4s.server.blaze.BlazeServerBuilder
import cats.implicits._
import scala.language.higherKinds

object AppServer extends IOApp {

  implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]

  private val helloWorldService = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, ${name*1000}.")
  }.orNotFound

  private val server: Resource[IO, Server[IO]] = BlazeServerBuilder[IO]
    .bindHttp(8080, "0.0.0.0")
    .withHttpApp(helloWorldService)
    .withNio2(true)
    .resource

  def run(args: List[String]): IO[ExitCode] =
    server.use(src =>
      Logger[IO].info("Server is Running on " + src.address) *>
      IO.never
    ).map(_ => ExitCode.Success)

}
