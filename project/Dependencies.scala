import sbt._

object Dependencies {

  val Common = Seq[ModuleID](
    "io.frees" %% "frees-core" % Versions.frees,
    "org.typelevel" %% "cats-core" % Versions.cats,
    "org.typelevel" %% "cats-free" % Versions.cats,
    "org.typelevel" %% "cats-effect" % Versions.catsEffects,
    "eu.timepit" %% "refined" % Versions.refined,
    "eu.timepit" %% "refined-cats" % Versions.refined
  )

  val Server = Common ++ Seq[ModuleID](
    "org.http4s" %% "http4s-blaze-server" % Versions.http4s,
    "org.http4s" %% "http4s-dsl" % Versions.http4s,
    "org.http4s" %% "http4s-circe" % Versions.http4s,
    "io.circe" %% "circe-generic" % Versions.circe,
    "io.circe" %% "circe-refined" % Versions.circe,
    "org.tpolecat" %% "doobie-core" % Versions.doobie,
    "org.tpolecat" %% "doobie-hikari" % Versions.doobie,
    "io.frees" %% "frees-http4s" % Versions.frees,
    "io.frees" %% "frees-logging" % Versions.frees,
    "io.chrisdavenport" %% "log4cats-core" % Versions.log4cats,
    "io.chrisdavenport" %% "log4cats-slf4j" % Versions.log4cats,
    "com.github.pureconfig" %% "pureconfig" % Versions.pureconfig,
    "io.github.jmcardon" %% "tsec-common" % Versions.tsecV,
    "io.github.jmcardon" %% "tsec-password" % Versions.tsecV,
    "io.github.jmcardon" %% "tsec-cipher-jca" % Versions.tsecV,
    "io.github.jmcardon" %% "tsec-cipher-bouncy" % Versions.tsecV,
    "eu.timepit" %% "refined-pureconfig" % Versions.refined
  )

  val Services = Common
}
