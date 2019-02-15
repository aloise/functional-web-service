package name.aloise.server

import java.util.concurrent.Executors
import pureconfig.generic.auto._
import cats.effect._
import name.aloise.db.connector.{DatabaseConnector, DatabaseConnectorConfiguration}
import name.aloise.http.api.{FaviconHttpApi, HealthHttpApi}
import name.aloise.utils.Logging
import org.http4s._
import org.http4s.server.Router
import pureconfig.module.catseffect._

import scala.concurrent.ExecutionContext

object AppServer extends IOApp with Logging[IO] {

  def routes[F[_] : Async : ContextShift](blockingFilesAccessEC: ExecutionContext): HttpRoutes[F] = Router[F](
    "/health" -> HealthHttpApi[F]().routes,
    "/" -> FaviconHttpApi[F](blockingFilesAccessEC).routes
  )

  def run(args: List[String]): IO[ExitCode] = {

    val dbConfig = loadConfigF[IO,DatabaseConnectorConfiguration]("db.configuration")

    val blockingFilesAccessEC =
      Resource.make(
        IO.delay(ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors())))
      )(ec => IO.delay(ec.shutdown()))

    val serverResource =
      for {
        blockingEC <- blockingFilesAccessEC
        allRoutes = routes[IO](blockingEC)
        db <- DatabaseConnector.open(dbConfig)
        server <- HttpServer(allRoutes)().server
      } yield server

    serverResource.use { srv =>
      for {
        _ <- log.info("Server is Running on " + srv.address)
        _ <- IO(Console.println("Press a key to exit."))
        _ <- IO(scala.io.StdIn.readLine())
        _ <- log.info("Shutting Down on key press")
      } yield ExitCode.Success
    }
  }

}
