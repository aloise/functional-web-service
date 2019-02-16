package name.aloise.server
import java.util.concurrent.Executors

import cats.effect._
import name.aloise.db.connector.{DatabaseConnector, DatabaseConnectorConfiguration}
import name.aloise.http.api.{FaviconHttpApi, HealthHttpApi, UserHttpApi}
import name.aloise.service.UserService
import org.http4s.HttpRoutes
import org.http4s.server.{Router, Server}

import scala.concurrent.ExecutionContext

abstract class AbstractAppServer[F[_]: Async] {

  private def routes(
      userService: UserService[F]
  )(
      blockingFilesAccessEC: ExecutionContext
  )(implicit CF: ContextShift[F]): HttpRoutes[F] = Router[F](
    "/health" -> HealthHttpApi[F]().routes,
    "/"       -> FaviconHttpApi[F](blockingFilesAccessEC).routes,
    "/user"   -> UserHttpApi[F](userService).routes
  )

  private val blockingFilesAccessEC =
    Resource.make(
      implicitly[Sync[F]].delay(
        ExecutionContext.fromExecutorService(
          Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors()))
      )
    )(ec => implicitly[Sync[F]].delay(ec.shutdown()))

  private def getUserService(dbConfig: DatabaseConnectorConfiguration)(
      implicit CF: ContextShift[F]): Resource[F, UserService[F]] =
    for {
      transactor <- DatabaseConnector.open(dbConfig)
      service = UserService.userServiceDoobieImpl(transactor)
    } yield service

  protected def serverResource(
      httpConfig: HttpServerConfiguration,
      dbConfig: DatabaseConnectorConfiguration)(
      implicit CF: ContextShift[F], T: Timer[F], CE: ConcurrentEffect[F]): Resource[F, Server[F]] =
    for {
      blockingEC   <- blockingFilesAccessEC
      userServices <- getUserService(dbConfig)
      allRoutes = routes(userServices)(blockingEC)
      server <- HttpServer(allRoutes)(httpConfig).server
    } yield server

}
