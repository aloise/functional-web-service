package name.aloise.utils

import cats.effect.{IO, Sync}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

trait Logging[F[_]] {
  @inline
  def log(implicit F: Sync[F]) = Logger[F](Slf4jLogger.getLogger[F])
}
