package name.aloise.server

import cats.effect._
import name.aloise.db.connector.DatabaseConnectorConfiguration
import name.aloise.utils.Logging
import pureconfig.module.catseffect._

object AppServer extends AbstractAppServer[IO] with IOApp with Logging[IO] {
  import pureconfig.generic.auto._

  private val dbConfig = Resource.liftF(loadConfigF[IO, DatabaseConnectorConfiguration]("db.configuration"))
  private val httpConfig = HttpServerConfiguration()

  def run(args: List[String]): IO[ExitCode] = {

    dbConfig flatMap (serverResource(httpConfig, _)) use { srv =>
      for {
        _ <- log.info("Server is Running on " + srv.address)
        _ <- IO(Console.println("Press a key to exit."))
        _ <- IO(scala.io.StdIn.readLine())
        _ <- log.info("Shutting Down on key press")
      } yield ExitCode.Success
    }
  }

}
