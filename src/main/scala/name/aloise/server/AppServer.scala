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
import name.aloise.utils.Logging

import scala.language.higherKinds

object AppServer extends IOApp with Logging[IO] {

  private val helloWorldService = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, ${name*19000}.")
  }

  private val healthService = HttpRoutes.of[IO] {
    case GET -> Root / "health" =>
      Ok("Healthy Welathy")
  }

  def run(args: List[String]): IO[ExitCode] =
    new HttpServer(healthService.orNotFound)().server.use { srv =>
      for {
        _ <- log.info("Server is Running on " + srv.address)
        _ <- IO.delay(Console.println("Press a key to exit."))
        _ <- IO.delay(scala.io.StdIn.readLine())
        _ <- log.info("Shutting Down on key press")
      } yield ExitCode.Success
    }

}
