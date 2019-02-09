package name.aloise.server

import java.util.concurrent.Executors

import cats.effect._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.{Router, Server}
import cats.implicits._
import name.aloise.utils.Logging

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

object AppServer extends IOApp with Logging[IO] {

  val blockingEcResource =
    Resource.make(IO.delay(ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))))(ec => IO.delay(ec.shutdown()))

  private val helloWorldService: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / name =>
      Ok(s"Hello, ${name*19000}.")
  }

  private val healthService = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok("Healthy Wealthy")
  }

  private def faviconService(blockingEc: ExecutionContext) = HttpRoutes.of[IO] {
    case req @ GET -> Root / "favicon.ico" =>
      StaticFile.fromResource("/favicon/favicon-32x32.png", blockingEc, Some(req)).getOrElseF(NotFound())
  }

  def routes(blockingEc: ExecutionContext): HttpRoutes[IO] = Router[IO](
    "/hello" -> helloWorldService,
    "/health" -> healthService,
    "/" -> faviconService(blockingEc)
  )


  def run(args: List[String]): IO[ExitCode] = {

    val serverResource =
      for {
        blockingEC <- blockingEcResource
        server <- HttpServer(routes(blockingEC))().server
      } yield server

    serverResource.use { srv =>
      for {
        _ <- log.info("Server is Running on " + srv.address)
        _ <- IO.delay(Console.println("Press a key to exit."))
        _ <- IO.delay(scala.io.StdIn.readLine())
        _ <- log.info("Shutting Down on key press")
      } yield ExitCode.Success
    }
  }

}
