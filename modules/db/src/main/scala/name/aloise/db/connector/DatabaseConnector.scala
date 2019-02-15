package name.aloise.db.connector
import cats.effect.{Async, ContextShift, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts

trait DatabaseConnector {
  def open[F[_] : Async : ContextShift](configurationF: F[DatabaseConnectorConfiguration]): Resource[F, HikariTransactor[F]] =
    for {
      configuration <- Resource.liftF(configurationF)
      connectionEC <- ExecutionContexts.fixedThreadPool[F](configuration.connectionPoolSize) // our connect EC
      transactionEC <- ExecutionContexts.cachedThreadPool[F]    // our transaction TE
      hikariTransactor <- HikariTransactor.newHikariTransactor[F](
        driverClassName = configuration.driverClassName,
        url = configuration.jdbcURL,
        user = configuration.user,
        pass = configuration.password,
        connectionEC,
        transactionEC
      )
      _  <- Resource.liftF(
        hikariTransactor.configure(ds => implicitly[Async[F]].pure {
          ds.setMaximumPoolSize(configuration.maxPoolSize)
          ds.setMinimumIdle(configuration.minPoolSize)
        })
      )
    } yield hikariTransactor
}

object DatabaseConnector extends DatabaseConnector

case class DatabaseConnectorConfiguration(
  jdbcURL: String, // jdbc:postgresql://host/database
  driverClassName: String, // "org.postgresql.Driver"
  user: String,
  password: String,
  connectionPoolSize: Int = 64,
  maxPoolSize: Int = 64,
  minPoolSize: Int = 8
)